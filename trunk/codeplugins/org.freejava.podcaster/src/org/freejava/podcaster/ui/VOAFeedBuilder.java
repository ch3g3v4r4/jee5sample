package org.freejava.podcaster.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.filters.Purifier;
import org.cyberneko.html.parsers.DOMParser;
import org.eclipse.core.runtime.IProgressMonitor;
import org.freejava.podcaster.Activator;
import org.freejava.podcaster.domain.PodcastItem;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;

public class VOAFeedBuilder {

    private static final String VOA_PROGRAMS_URL = "http://www.voanews.com/specialenglish/programs.cfm";
    private static final String VOA_ARCHIVE_URL = "http://www.voanews.com/specialenglish/archive/index.cfm";
    private static final String XHTML_NS_URI = "http://www.w3.org/1999/xhtml";


    public static final String AGRICULTURE_REPORT = "AGRICULTURE_REPORT";
    public static final String AMERICAN_MOSAIC = "AMERICAN_MOSAIC";
    public static final String AMERICAN_STORIES = "AMERICAN_STORIES";
    public static final String DEVELOPMENT_REPORT = "DEVELOPMENT_REPORT";
    public static final String ECONOMICS_REPORT = "ECONOMICS_REPORT";
    public static final String EDUCATION_REPORT = "EDUCATION_REPORT";
    public static final String EXPLORATIONS = "EXPLORATIONS";
    public static final String HEALTH_REPORT = "HEALTH_REPORT";
    public static final String IN_THE_NEWS = "IN_THE_NEWS";
    public static final String PEOPLE_IN_AMERICA = "PEOPLE_IN_AMERICA";
    public static final String SCIENCE_IN_THE_NEWS = "SCIENCE_IN_THE_NEWS";
    public static final String THE_MAKING_OF_A_NATION = "THE_MAKING_OF_A_NATION";
    public static final String THIS_IS_AMERICA = "THIS_IS_AMERICA";
    public static final String WORDS_AND_THEIR_STORIES = "WORDS_AND_THEIR_STORIES";
    public static final String WORDMASTER = "WORDMASTER";

    private List<String> processedArticles = new ArrayList<String>();

    public static void main(String[] args) throws Exception {
        String url = "http://www.voanews.com/specialenglish/archive/2008-10/2008-10-27-voa7.cfm";
        VOAFeedBuilder b = new VOAFeedBuilder();
        PodcastItem i = b.getArticle(url, "1", new HashMap<String, PodcastItem>());
        System.out.println(i.getCategory());
    }
    private static class HTMLNamespaceContext implements NamespaceContext {
        public String getNamespaceURI(String prefix) {
            if (prefix == null) throw new NullPointerException("Null prefix");
            else if (prefix.equals(XMLConstants.XML_NS_PREFIX))
                return XMLConstants.XML_NS_URI;
            else if (prefix.equals("html"))
                return XHTML_NS_URI;
            else
                return XMLConstants.NULL_NS_URI;
        }
        public String getPrefix(String namespaceURI) {
            return null;
        }
        @SuppressWarnings("unchecked")
        public Iterator getPrefixes(String namespaceURI) {
            return null;
        }
    }

    private Object[] parseHTML(String urlStr) throws Exception {
        Object[] result = new Object[2];
        System.out.println("Parse HTML for URL: " + urlStr);

        DOMParser domParser = new DOMParser();
        domParser.setFeature(
                "http://cyberneko.org/html/features/insert-namespaces", true);
        domParser.setProperty(
                "http://cyberneko.org/html/properties/names/elems", "lower");
        // Fix wrong namespaces
        XMLDocumentFilter[] filters = { new Purifier() };
        domParser.setProperty("http://cyberneko.org/html/properties/filters", filters);

        URL url = new URL(urlStr);
        InputStream istr = null;
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        try {
            istr = url.openStream();
            IOUtils.copy(istr, bo);
        } finally {
            if (istr != null) IOUtils.closeQuietly(istr);
        }
        byte[] array = bo.toByteArray();
        ByteArrayInputStream bi = new ByteArrayInputStream(array);
        InputSource is = new InputSource();
        is.setSystemId(urlStr);
        is.setByteStream(bi);
        domParser.parse(is);

        HTMLDocument document = (HTMLDocument) domParser.getDocument();
        result[0] = document;
        result[1] = array;
        return result;
    }

    public List<PodcastItem> getCurrentEntries(
            String feedId,
            IProgressMonitor monitor, Map<String, PodcastItem> reference) throws Exception {
        List<PodcastItem> result = new ArrayList<PodcastItem>();

        HTMLDocument document = (HTMLDocument) parseHTML(VOA_PROGRAMS_URL)[0];

        XPathExpression expr = getHTMLXPath().compile("//html:div/html:ul/html:li/html:a");
        Object atags = expr.evaluate(document, XPathConstants.NODESET);

        NodeList nodes = (NodeList) atags;
        List<String> errorUrls  = new ArrayList<String>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Element aElement = (Element) nodes.item(i);
            String href = aElement.getAttribute("href");
            String urlStr = makeAbsoluteUrl(VOA_PROGRAMS_URL, href);
            if (!processedArticles.contains(urlStr)) {
                try {
                    PodcastItem entry = getArticle(urlStr, feedId, reference);
                    result.add(entry);
                    processedArticles.add(urlStr);
                } catch (Exception e) {
                    errorUrls.add(urlStr);
                    Activator.logWarning("VOA: getCurrentEntries: 1st error:" + urlStr, e);
                }
            }
            if (monitor.isCanceled()) return result;
        }
        for (String urlStr : errorUrls) {
            if (!processedArticles.contains(urlStr)) {
                try {
                    PodcastItem entry = getArticle(urlStr, feedId, reference);
                    result.add(entry);
                    processedArticles.add(urlStr);
                } catch (Exception e) {
                    Activator.logError("VOA: getCurrentEntries: 2nd error:" + urlStr, e);
                }
            }
            if (monitor.isCanceled()) return result;
        }
        return result;
    }

    public PodcastItem getArticle(String url,
            String feedId, Map<String, PodcastItem> reference) throws Exception {

        if (reference.containsKey(url)) {
            PodcastItem copy = reference.get(url);
            PodcastItem entry = new PodcastItem(UUID.randomUUID().toString(), copy.getTitle(), copy.getDescription(),
                    copy.getPublishDate(), feedId, copy.getLink(), copy.getGuid(),
                    copy.getEnclosureUrl(), copy.getEnclosureDiskpath(),
                    copy.getEnclosureLength(), copy.getEnclosureType(), copy.getCategory());
            return entry;
        };

        Object[] tmp = parseHTML(url);
        HTMLDocument document = (HTMLDocument) tmp[0];
        byte[] documentArray = (byte[]) tmp[1];

        // title
        String title;
        String docString = new String(documentArray, "UTF-8");
        int titleTagPos = docString.toLowerCase().indexOf("<title>");
        int endTitleTagPos = docString.toLowerCase().indexOf("</title>");
        if (titleTagPos >= 0 && endTitleTagPos > titleTagPos) {
            // getTextContent() returns wrong Unicode characters (http://www.voanews.com/specialenglish/archive/2008-01/2008-01-01-voa2.cfm)
            // so we have to use String.indexOf() to copy title
            title = docString.substring(titleTagPos + 7, endTitleTagPos);
            title = StringEscapeUtils.unescapeHtml(title);
        } else {
            title = (String) getHTMLXPath().evaluate(
                    "//html:head/html:title/text()", document, XPathConstants.STRING);
        }
        title = htmlTrim(title);

        // link
        String link = url;

        // guid
        String guid = url;

        // publishDate
        Date publishDate = null;
        Element contentElem = (Element) getHTMLXPath().evaluate(
                "//html:body/html:div/html:table[2]/html:tr/html:td[2] | //html:body/html:div/html:table[2]/html:tbody/html:tr/html:td[2]",
                document, XPathConstants.NODE);
        String text = contentElem.getTextContent();
        text = htmlTrim(text);
        Pattern pattern = Pattern.compile("\\d+\\s+((January)|(February)|(March)|(April)|(May)|(June)|(July)|(August)|(September)|(October)|(November)|(December))\\s+\\d+");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String pubDateStr = matcher.group();
            DateFormat formatter = new SimpleDateFormat("dd MMMMMM yyyy");
            formatter.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("EST")));
            publishDate = (Date)formatter.parse(pubDateStr);
        }

        // attachment
        String enclosureUrl = null;
        String enclosureDiskpath = null;
        Long enclosureLength = null;
        String enclosureType = null;
        NodeList nodes = (NodeList) getHTMLXPath().evaluate("//html:a", document, XPathConstants.NODESET);
        Element aDownloadTag = null;
        for (int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element) nodes.item(i);
            String etext = e.getTextContent();
            if ((etext.indexOf("MP3") != -1 || etext.indexOf("RealAudio") != -1)
                    && etext.indexOf("Download") != -1
                    && etext.indexOf("Listen") == -1) {
                aDownloadTag = e;
                break;
            }
        }
        if (aDownloadTag == null) {
            for (int i = 0; i < nodes.getLength(); i++) {
                Element e = (Element) nodes.item(i);
                String href = e.getAttribute("href");
                String etext = e.getTextContent();
                if ((etext.indexOf("MP3") != -1) && href != null && href.endsWith(".mp3")) {
                    aDownloadTag = e;
                    break;
                }
            }
        }
        if (aDownloadTag != null) {
            String href = aDownloadTag.getAttribute("href");
            if (href.startsWith("javascript:")) {
                Pattern pattern1 = Pattern.compile("http://.+\\.mp3");
                Matcher matcher1 = pattern1.matcher(href);
                if (matcher1.find()) {
                    href = matcher1.group();
                }
            }
            String attachmentUrlStr = makeAbsoluteUrl(url, href);
            attachmentUrlStr = StringUtils.replace(attachmentUrlStr, " ", "%20");
            enclosureUrl = attachmentUrlStr;
            String[] typeAndLength = getTypeAndLength(attachmentUrlStr);
            enclosureLength = Long.valueOf(typeAndLength[1]);
            enclosureType = typeAndLength[0];
        }

        // Description
        String description = url;

        // Script
        String category = getCategory(url, document, docString);

        PodcastItem entry = new PodcastItem(UUID.randomUUID().toString(), title, description,
                publishDate, feedId, link, guid,
                enclosureUrl, enclosureDiskpath,
                enclosureLength, enclosureType, category);

        return entry;

    }

    private String getCategory(String url, HTMLDocument document, String docString) throws Exception {
        String content = getScriptContent(url, document);
        String category = determineCategory(content);
        if (category == null) {
            category = determineCategory(docString);
        }
        if (category == null) {
            System.err.println("Cannot determine category for:" + url);
        }

        return category;
    }

    private String determineCategory(String content) {
        String category = null;
        Map<String, String> keyword2Category = new Hashtable<String, String>();
        keyword2Category.put("Agriculture Report", AGRICULTURE_REPORT);
        keyword2Category.put("American Mosaic", AMERICAN_MOSAIC);
        keyword2Category.put("American Stories", AMERICAN_STORIES);
        keyword2Category.put("Development Report", DEVELOPMENT_REPORT);
        keyword2Category.put("Economics Report", ECONOMICS_REPORT);
        keyword2Category.put("Education Report", EDUCATION_REPORT);
        keyword2Category.put("Explorations", EXPLORATIONS);
        keyword2Category.put("Health Report", HEALTH_REPORT);
        keyword2Category.put("In the News", IN_THE_NEWS);
        keyword2Category.put("People In America", PEOPLE_IN_AMERICA);
        keyword2Category.put("Science In the News", SCIENCE_IN_THE_NEWS);
        keyword2Category.put("The Making of a Nation", THE_MAKING_OF_A_NATION);
        keyword2Category.put("This Is America", THIS_IS_AMERICA);
        keyword2Category.put("Words and Their Stories", WORDS_AND_THEIR_STORIES);
        keyword2Category.put("WORDMASTER", WORDMASTER);

        Map<String, Integer> categories = new Hashtable<String, Integer>();
        for (String key : keyword2Category.keySet()) {
            if (content.indexOf(key.toUpperCase()) != -1) {
                categories.put(keyword2Category.get(key), content.indexOf(key.toUpperCase()));
            }
        }
        if (categories.isEmpty()) {
            for (String key : keyword2Category.keySet()) {
                if (content.indexOf(key) != -1) {
                    categories.put(keyword2Category.get(key), content.indexOf(key));
                }
            }
        }
        if (categories.isEmpty()) {
            for (String key : keyword2Category.keySet()) {
                if (content.toUpperCase().indexOf(key.toUpperCase()) != -1) {
                    categories.put(keyword2Category.get(key),
                            content.toUpperCase().indexOf(key.toUpperCase()));
                }
            }
        }
        if (categories.size() == 1) {
            category = categories.keySet().iterator().next();
            System.out.println("Category:" + category);
        } else if (categories.size() == 2
                && categories.keySet().contains(SCIENCE_IN_THE_NEWS)
                && categories.keySet().contains(IN_THE_NEWS)) {
            category = SCIENCE_IN_THE_NEWS;
            System.out.println("Category:" + category);
        } else if (categories.size() > 1) {
            Integer anIndex = -1;
            String aCategory = null;
            for (String c : categories.keySet()) {
                Integer i = categories.get(c);
                if (i > anIndex) aCategory = c;
            }
            category = aCategory;
            System.out.println("Category:" + category);
        }
        return category;
    }

    private String getScriptContent(String url, HTMLDocument document)
            throws Exception {
        String content;
        Element bodySpan = (Element) getHTMLXPath().evaluate(
                "//html:span[@class='body']",
                document, XPathConstants.NODE);
        NodeList imgs = bodySpan.getElementsByTagName("img");
        for (int i = 0; i < imgs.getLength(); i++) {
            Element img = (Element) imgs.item(i);
            String src = img.getAttribute("src");
            src = makeAbsoluteUrl(url, src);
            img.setAttribute("src", src);
        }
        NodeList links = bodySpan.getElementsByTagName("a");
        for (int i = 0; i < links.getLength(); i++) {
            Element hyperlink = (Element) links.item(i);
            String href = hyperlink.getAttribute("href");
            if (href != null) {
                href = makeAbsoluteUrl(url, href);
                hyperlink.setAttribute("href", href);
            }
        }
        StringWriter sw = new StringWriter();
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer = tfactory.newTransformer();
        Properties oprops = new Properties();
        oprops.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
        serializer.setOutputProperties(oprops);
        serializer.transform(new DOMSource(bodySpan), new StreamResult(sw));
        content = sw.getBuffer().toString();

        return content;
    }

    private String makeAbsoluteUrl(String url, String href)
            throws MalformedURLException {
        String urlStr;
        if (href.indexOf(':') == -1) {
            URL attachmentUrl = new URL(new URL(url), href);
            urlStr = attachmentUrl.toExternalForm();
        } else {
            urlStr = href;
        }
        urlStr = StringUtils.replace(urlStr, " ", "%20");
        return urlStr;
    }


    private String[] getTypeAndLength(String url) throws Exception {
        String[] result = new String[2];
        url = StringUtils.replace(url, " ", "%20");
        System.out.println("Get type and length for attachment URL: " + url);
        URLConnection urlConnection = new URL(url).openConnection();
        if (urlConnection instanceof HttpURLConnection) {
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setRequestMethod("HEAD");
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                String type = urlConnection.getHeaderField("Content-Type");
                String length = String.valueOf(urlConnection.getContentLength());
                result[0] = type;
                result[1] = length;
            }
        } else if (url.contains(".mp3")) {
            result[0] = "audio/mpeg";
            result[1] = "0";
        }
        return result;
    }
    private String htmlTrim(String htmlText) {
        return htmlText.replace('\u00a0', ' ').trim();
    }
    private XPath getHTMLXPath() {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(new HTMLNamespaceContext());
        return xpath;
    }

    public List<String> getArchiveWeekLinks() throws Exception {
        List<String> result = new ArrayList<String>();

        HTMLDocument document = (HTMLDocument) parseHTML(VOA_ARCHIVE_URL)[0];
        Element contentElement = (Element) getHTMLXPath().evaluate(
                "//html:body/html:div/html:table[2]/html:tr/html:td[2] | //html:body/html:div/html:table[2]/html:tbody/html:tr/html:td[2]",
                document, XPathConstants.NODE);
        NodeList nodes =  contentElement.getElementsByTagName("a");
        TreeMap<Date, String> weekLinks = new TreeMap<Date, String>();
        String patternStr = "[0-9]+/[0-9]+/[0-9]+";
        Pattern datePattern = Pattern.compile(patternStr);
        for (int i = 0; i < nodes.getLength(); i++) {
            Element elem = (Element) nodes.item(i);
            String href =  elem.getAttribute("href");
            Matcher matcher = datePattern.matcher(href);
            if (matcher.find()) {
                String dateStr = matcher.group();
                Date date = DateUtils.parseDate(dateStr, new String[]{"M/dd/yyyy"});
                href = fixInvalidMonthHref(href);
                String weekurlStr = makeAbsoluteUrl(VOA_ARCHIVE_URL, href);
                weekLinks.put(date, weekurlStr);
            } else {
                Activator.logWarning("Invalid archive week URL:" + href);
            }
        }
        result.addAll(weekLinks.values());
        Collections.reverse(result);
        return result;
    }

    public List<PodcastItem> getArticles(String weekLink,
            String feedId, IProgressMonitor monitor, Map<String, PodcastItem> reference) throws Exception {
        List<PodcastItem> result = new ArrayList<PodcastItem>();
        List<String> urls = getArchiveArticleLinks(weekLink);
        List<String> errorUrls  = new ArrayList<String>();
        for (String url : urls) {
            if (!processedArticles.contains(url)) {
                try {
                    PodcastItem entry = getArticle(url, feedId, reference);
                    result.add(entry);
                    processedArticles.add(url);
                } catch (Exception e) {
                    errorUrls.add(url);
                    Activator.logWarning("VOA: getArticles: 1st error:" + url, e);
                }
            }
            if (monitor.isCanceled()) return result;
        }
        for (String urlStr : errorUrls) {
            if (!processedArticles.contains(urlStr)) {
                try {
                    PodcastItem entry = getArticle(urlStr, feedId, reference);
                    result.add(entry);
                    processedArticles.add(urlStr);
                } catch (Exception e) {
                    Activator.logError("VOA: getArticles: 2nd error:" + urlStr, e);
                }
            }
            if (monitor.isCanceled()) return result;
        }
        return result;
    }

    private List<String> getArchiveArticleLinks(String weekLink) throws Exception {
        List<String> result = new ArrayList<String>();

        HTMLDocument document = (HTMLDocument) parseHTML(weekLink)[0];
        Element firstH3 = (Element) getHTMLXPath().evaluate(
                "//html:h3[1]", document, XPathConstants.NODE);
        Node currentNode = firstH3;
        while (currentNode != null) {
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) currentNode;
                NodeList aNodes = elem.getElementsByTagName("a");
                for (int i = 0; i < aNodes.getLength(); i++) {
                    Element a = (Element) aNodes.item(i);
                    String href =  a.getAttribute("href");
                    href = fixInvalidMonthHref(href);
                    String archiveArticleUrlStr = makeAbsoluteUrl(weekLink, href);
                    archiveArticleUrlStr = StringUtils.replace(archiveArticleUrlStr, " ", "%20");
                    result.add(archiveArticleUrlStr);
                }
            }
            currentNode = currentNode.getNextSibling();
        }
        return result;
    }

    private String fixInvalidMonthHref(String href) throws Exception {
        String patternStr = "index.cfm\\?month=[0-9]+/[0-9]+/[0-9]+";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(href);
        if (matcher.matches()) {
            String arg = href.substring("index.cfm?month=".length());
            href = "index.cfm?month=" + URLEncoder.encode(arg, "UTF-8");
        }
        return href;
    }
}
