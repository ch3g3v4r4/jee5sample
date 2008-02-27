package org.builder.eclipsebuilder.beans;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.builder.eclipsebuilder.beans.Configuration.BuildType;

public class PartBuilderHelper implements PartBuilder {

    private static Logger logger = Logger.getLogger(PartBuilderHelper.class);

    protected WebBrowser webBrowser;
    private DownloadManager downloadManager;
    private String downloadPage;
    private String artifactId;
    protected List<String> features;

    protected List<PartBuilder> parts;

    public void setWebBrowser(WebBrowser webBrowser) {
        this.webBrowser = webBrowser;
    }

    public void setDownloadManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    public String getDownloadPage() {
        return downloadPage;
    }

    public void setDownloadPage(String downloadPage) {
        this.downloadPage = downloadPage;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public void setParts(List<PartBuilder> parts) {
        this.parts = parts;
    }

    public void build(EclipseBuilderContext context) throws Exception {

        if (this.parts != null) {
            // Download dependencies first
            for (PartBuilder builder : this.parts) {
                logger.info("Building dependencies using " + builder.getClass().getName());
                builder.build(context);
            }
        }

        logger.info("Looking for the " + getArtifactId() + " hyperlink.");
        String[] downloadLinkAndChecksumLink = getDownloadAndChecksumLinks(
                getDownloadPage(), getArtifactId(),
                context.getBuildType());
        String downloadLink = downloadLinkAndChecksumLink[0];
        String checksumLink = downloadLinkAndChecksumLink[1];
        logger.info(getArtifactId() + " hyperlink: " + downloadLink + "; checksum link:" + checksumLink);

        File file = downloadAndCheck(downloadLink, checksumLink, context.getCacheHome());

        // install to target folder
        installPart(file, context.getEclipseHome(), true);
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private boolean verifyChecksum(File file, String url) throws Exception {
        boolean verify = false;
        if (url != null) {
            MessageDigest md = null;
            if (url.toString().endsWith(".md5")) {
                md = MessageDigest.getInstance("MD5");
            }
            if (url.toString().endsWith(".sha1")) {
                md = MessageDigest.getInstance("SHA-1");
            }
            String fileContent = webBrowser.getUrlContentAsText(url);
            if (md != null && fileContent != null) {
                String expectedChecksum = fileContent.split(" ")[0];
                String checksum = digest(file, md);
                verify = expectedChecksum.equals(checksum);
            }
        }
        if (!verify && (file.getName().endsWith(".zip") || file.getName().endsWith(".jar"))) {
            // try to test zip integrity
            File temp = File.createTempFile("tmp", ".tmp");
            temp.delete();
            temp.mkdir();
            try {
                unzip(file, temp, true);
                verify = true;
            } catch (Exception e) {
            }
            deleteDir(temp);
        }
        return verify;
    }

    private String digest(File file, MessageDigest md) throws Exception {
        String result = null;
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            byte buf[] = new byte[8192];
            int bytes = 0;
            while ((bytes = bis.read(buf)) != -1) {
                md.update(buf, 0, bytes);
            }
            char[] dg = Hex.encodeHex(md.digest());
            result = new String(dg);
        } finally {
            if (bis != null) {
                bis.close();
            }
        }
        return result;
    }

    private Object[] getNameAndSize(URL url) throws Exception {
        return this.webBrowser.getFileNameAndSize(url);
    }

    private void downloadFile(String downloadLink, File file, Long fileSize)
            throws Exception {
        downloadManager.setUrl(new URL(downloadLink));
        downloadManager.setFile(file);
        downloadManager.setFileSize(fileSize);
        downloadManager.setMaxThreads(10);
        downloadManager.setMaxTries(100);

        Thread thread = new Thread(downloadManager);
        logger.info("Starting download manager.");
        thread.start();
        logger.info("Waiting download manager to stop.");
        thread.join();
        if (!downloadManager.getErrors().isEmpty()) {
            throw downloadManager.getErrors().get(0);
        }
        logger.info("Download manager stopped.");
    }

    protected void installPart(File partFile, File eclipseParentDir, boolean overwrite) throws Exception {
        File eclipse = new File(eclipseParentDir, "eclipse");
        eclipse.mkdir();
        if (partFile.getName().endsWith(".jar")) {
            File plugins = new File(eclipse, "plugins");
            plugins.mkdir();
            File destFile = new File(plugins, partFile.getName());
            FileUtils.copyFile(partFile, destFile);
        } else if (partFile.getName().endsWith(".zip")) {
            List<String> fileNames = listZip(partFile);
            if (fileNames.contains("site.xml")) {
                File temp = File.createTempFile("tmp", ".tmp");
                temp.delete();
                temp.mkdir();
                unzip(partFile, temp, true);
                installSiteUpdate(temp, eclipse, this.features, overwrite);
                deleteDir(temp);
            } else if (fileNames.contains("eclipse/")) {
                unzip(partFile, eclipseParentDir, overwrite);
            } else if (fileNames.contains("plugins/") && !fileNames.contains("bin/")) {
                unzip(partFile, eclipse, overwrite);
            } else if (containsPattern(fileNames, "[^/]+/plugin.xml")) {
                File plugins = new File(eclipse, "plugins");
                plugins.mkdir();
                unzip(partFile, plugins, overwrite);
            } else if (containsPattern(fileNames, "[^/]+/site.xml")) {
                File temp = File.createTempFile("tmp", ".tmp");
                temp.delete();
                temp.mkdir();
                unzip(partFile, temp, true);
                String[] children = temp.list();
                File updateDir = new File(temp, children[0]);
                installSiteUpdate(updateDir, eclipse, this.features, overwrite);
                deleteDir(temp);
            } else {
                String fileName = partFile.getName();
                if (fileName.endsWith(".zip")) {
                    fileName = fileName.substring(0, fileName.length() - ".zip".length());
                }
                File dir = new File(eclipseParentDir, fileName);
                dir.mkdir();
                unzip(partFile, dir, overwrite);
            }
        }
    }

    private boolean containsPattern(List<String> strings, String pattern) {
        boolean found = false;
        Pattern p = Pattern.compile(pattern);
        for (String s: strings) {
            if (p.matcher(s).matches()) {
                found = true;
                break;
            }
        }
        return found;
    }

    private void installSiteUpdate(File siteUpdateFolder, File eclipse, List<String> features, boolean overwrite) throws Exception {
        // http://publib.boulder.ibm.com/infocenter/wchelp/v6r0m0/index.jsp?topic=/com.ibm.commerce.telesales.developer.doc/tasks/ttrdcreateupdatesiteexample.htm
        // http://dev.eclipse.org/newslists/news.eclipse.platform/msg66561.html
        // java -jar plugins/org.eclipse.equinox.launcher_<version>.jar -application  org.eclipse.update.core.standaloneUpdate -command search -from remote_site_url
        // java -jar plugins/org.eclipse.equinox.launcher_<version>.jar -application  org.eclipse.update.core.standaloneUpdate -command install -featureId feature_id -version version -from remote_site_url [-to target_site_dir]

        // List features and versions
        File plugins = new File(eclipse, "plugins");
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.getName().startsWith("org.eclipse.equinox.launcher_");
            }
        };
        File[] files = plugins.listFiles(fileFilter);
        Process p = Runtime.getRuntime().exec(
                new String[]{
                        getJavaCommand(), "-jar", files[0].getAbsolutePath(),
                        "-application",  "org.eclipse.update.core.standaloneUpdate",
                        "-command", "search",
                        "-from", siteUpdateFolder.toURL().toString()});
        // Get the input stream and read from it
        InputStream in = p.getInputStream();
        StringBuffer sb = new StringBuffer();
        int c;
        while ((c = in.read()) != -1) {
            sb.append((char)c);
        }
        in.close();
        p.waitFor();

        Map<String, String> feature2Version = new HashMap<String, String>();
        String patternStr = "^(.*)$";
        Pattern pattern = Pattern.compile(patternStr, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(sb.toString());
        while (matcher.find()) {
            String line = matcher.group(1);
            String[] splits = line.split(" ");
            String version = splits[splits.length - 1];
            Pattern numPattern = Pattern.compile("\\d");
            if (numPattern.matcher(version).find()) {
                feature2Version.put(splits[splits.length - 2], splits[splits.length - 1]);
            }
        }
        for (String feature: features) {
            p = Runtime.getRuntime().exec(
                    new String[]{
                            getJavaCommand(), "-jar", files[0].getAbsolutePath(),
                            "-application",  "org.eclipse.update.core.standaloneUpdate",
                            "-command", "install",
                            "-featureId", feature,
                            "-version", feature2Version.get(feature),
                            "-from", siteUpdateFolder.toURL().toString()});
            p.waitFor();

        }
    }

    private String getJavaCommand() {
        String osname = System.getProperty("os.name").toLowerCase();
        String commandName;
        if (osname.indexOf("windows") >= 0) {
            commandName = "javaw.exe";
        } else {
            commandName = "java";
        }
        return System.getProperty("java.home") + File.separator + "bin" + File.separator + commandName;
    }

    private List<String> listZip(File zipFile) throws Exception {
        List<String> names = new ArrayList<String>();
        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {
            String zipEntryName = ((ZipEntry)entries.nextElement()).getName();
            names.add(zipEntryName);
        }
        return names;
    }

    protected void unzip(File zipFile, File targetFolder, boolean overwrite)
            throws Exception {
        logger.info("Begin unzipping file:" + zipFile.getName()
                + " to folder: " + targetFolder);

        ZipEntry entry;
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(zipFile));
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String destFN = targetFolder.getAbsolutePath()
                            + File.separator + entry.getName();
                    createDirectories(destFN);
                    File targetFile = new File(destFN);
                    if (!targetFile.exists() || overwrite) {
                        FileOutputStream fos = new FileOutputStream(destFN);
                        try {
                            IOUtils.copy(zis, fos);
                        } finally {
                            fos.close();
                        }
                    }
                }
            }
        } finally {
            if (zis != null)
                zis.close();
        }

        logger.info("Unzipping completed.");
    }

    private void createDirectories(String filePath) {
        File file = new File(filePath);

        // List all directories to be created
        File directory = file.getParentFile();
        List<File> directories = new ArrayList<File>();
        while (directory != null && !directory.exists()) {
            directories.add(directory);
            directory = directory.getParentFile();
        }

        // Create directories (in reverse order)
        Collections.reverse(directories);
        for (File dir : directories) {
            dir.mkdir();
        }
    };

    protected File downloadAndCheck(String downloadLink, String checksumLink, File cacheFolder) throws Exception {
        // If the file is already downloaded, verify it checksum to determine download again or skip
        Object[] nameAndSize = getNameAndSize(new URL(downloadLink));
        String fileName = (String) nameAndSize[0];
        Long downloadSize = (Long) nameAndSize[1];
        logger.info("File name: " + fileName + "; file size:" + downloadSize);
        File file = new File(cacheFolder, fileName);

        boolean fileExist = file.exists();
        boolean checksumValid = false;

        if (fileExist) {
            logger.info("Verifying checksum...");
            checksumValid = verifyChecksum(file, checksumLink);
        }

        if (!fileExist || !checksumValid) {
            logger.info("File is not found in cache or checksum is incorrect, will download file.");
            downloadFile(downloadLink, file, downloadSize);
            fileExist = file.exists();
            if (fileExist) {
                logger.info("File is downloaded to location:" + file.getAbsolutePath());
                logger.info("Verifying checksum...");
                checksumValid = verifyChecksum(file, checksumLink);
            } else {
                logger.error("Failed to download file.");
                throw new Exception("Failed to download file!");
            }
        }

        if (!checksumValid) {
            logger.warn("Failed to verify file integrity.");
        } else {
            logger.info("File integrity is good.");
        }
        return file;
    }

    protected String[] getDownloadAndChecksumLinks(String url, String artifactId,
            BuildType buildType) throws Exception {

        String downloadArtifactUrl = null;
        LinkedHashMap<String, List<String>> urlsOnDownloadPath = new LinkedHashMap<String, List<String>>();

        String currentUrl = url;
        List<String> contentTypeList;
        List<String> urlList;
        List<String> visitedLinks = new ArrayList<String>();
        do {
            logger.info("Openning hyperlink: " + currentUrl);
            urlList = new ArrayList<String>();
            contentTypeList = new ArrayList<String>();
            webBrowser.getLinksAndContenType(currentUrl, urlList, contentTypeList);
            visitedLinks.add(currentUrl);
            urlList.removeAll(visitedLinks);
            String contentType = contentTypeList.get(0);
            if (contentType.startsWith("text/html")) {
                urlsOnDownloadPath.put(currentUrl, new LinkedList<String>(urlList));
                currentUrl = selectBestNextUrl(artifactId, buildType, urlList);
                logger.info("Selected next hyperlink: " + currentUrl);
            } else {
                urlsOnDownloadPath.put(currentUrl, null);
                downloadArtifactUrl = currentUrl;
                break;
            }

        } while (true);


        String downloadArtifactChecksumUrl = null;
        if (downloadArtifactUrl != null) {
            String downloadFileName = DownloadLinkUtils.parseDownloadLink(downloadArtifactUrl).getFileName();
            String checksumFileName1 = downloadFileName + ".md5";
            String checksumFileName2 = downloadFileName + ".sha1";
            String checksumFileName3 = downloadFileName.substring(0, downloadFileName.lastIndexOf('.')) + ".md5";

            List<String> reversePath = new ArrayList<String>();
            for (Iterator<String> it = urlsOnDownloadPath.keySet().iterator(); it.hasNext();) {
                reversePath.add(it.next());
            }
            Collections.reverse(reversePath);
            outer:
            for (String pathElem : reversePath) {
                List<String> links = urlsOnDownloadPath.get(pathElem);
                if (links != null) {
                    for (String link : links) {
                        if (link.contains(checksumFileName1) || link.contains(checksumFileName2)|| link.contains(checksumFileName3)) {
                            downloadArtifactChecksumUrl = link;
                            break outer;
                        }
                    }
                }
            }
        }
        return new String[] {downloadArtifactUrl, downloadArtifactChecksumUrl};
    }

    private String selectBestNextUrl(String artifactId, BuildType buildType,
            List<String> urlList) throws Exception {
        String result;

        List<String> links = filter(urlList, artifactId, buildType);
        if (!links.isEmpty()) {
            sortByArtifactVersion(links);
            result = links.get(links.size() - 1);
        } else {
            result = null;
        }

        return result;
    }

    private void sortByArtifactVersion(List<String> links) {
        Collections.sort(links, new Comparator<String>() {
            public int compare(String l1, String l2) {
                int c;
                Artifact a1 = null;
                Artifact a2 = null;
                try {
                    a1 = DownloadLinkUtils.parseDownloadLink(l1);
                } catch (Exception e) {
                    logger.error("Cannot parse!", e);
                }
                try {
                    a2 = DownloadLinkUtils.parseDownloadLink(l2);
                } catch (Exception e) {
                    logger.error("Cannot parse!", e);
                }
                if (a1 == null && a2 == null) {
                    Pattern p = Pattern.compile("\\d+(\\.\\d+)*");
                    Matcher m1 = p.matcher(l1);
                    Matcher m2 = p.matcher(l2);
                    if (m1.find() && m2.find()) {
                        String v1 = m1.group();
                        String v2 = m2.group();
                        if (v1.indexOf('.') == -1 && v2.indexOf('.') == -1 && v1.length() != v2.length()) {
                            int maxLength = Math.max(v1.length(), v2.length());
                            while (v1.length() < maxLength) v1 += '0';
                            while (v2.length() < maxLength) v2 += '0';
                        }
                        c = v1.compareTo(v2);
                    } else c = l1.compareTo(l2);
                } else if (a1 == null && a2 != null) c = -1;
                else if (a1 != null && a2 == null) c = 1;
                else {
                    c = compareVersion(a1.getVersion(), a2.getVersion());
                }
                return c;
            }

        });
    }

    private static int compareVersion(String v1, String v2) {
        int c;
        if (v1 == v2) c = 0;
        else if (v1 == null && v2 != null) c = -1;
        else if (v1 != null && v2 == null) c = 1;
        else {
            // compare to non-empty version numbers
            // standardize versions like 3.4.0M5 and 3.4M5 before comparing
            Pattern p = Pattern.compile("^\\d+(\\.\\d+)+");
            Matcher m1 = p.matcher(v1);
            Matcher m2 = p.matcher(v2);
            String v1g1 = "0";
            String v1g2 = "Z";
            if (m1.find()) {
                v1g1 = m1.group(0);
                v1g2 = v1.substring(v1g1.length());
            }
            String v2g1 = "0";
            String v2g2 = "Z";
            if (m2.find()) {
                v2g1 = m2.group(0);
                v2g2 = v2.substring(v2g1.length());
            }
            List<String> v1l = new LinkedList<String>(Arrays.asList(v1g1.split("\\.")));
            List<String> v2l = new LinkedList<String>(Arrays.asList(v2g1.split("\\.")));
            int maxSize = Math.max(v1l.size(), v2l.size());
            List<Integer> v1in = new ArrayList<Integer>(v1l.size());
            List<Integer> v2in = new ArrayList<Integer>(v2l.size());
            for (String s : v1l) {
                v1in.add(Integer.parseInt(s));
            }
            for (String s : v2l) {
                v2in.add(Integer.parseInt(s));
            }
            while (v1in.size() < maxSize) v1in.add(0);
            while (v2in.size() < maxSize) v2in.add(0);
            c = 0;
            int i = 0;
            while (i < maxSize && c == 0) {
                c = v1in.get(i) - v2in.get(i);
                i++;
            }
            if (c == 0) {
                if (v1g2 == null || v1g2.length() == 0) c = 1;
                else if (v2g2 == null || v2g2.length() == 0) c = -1;
                else c = v1g2.compareTo(v2g2);
            }
        }
        return c;
    }

    protected List<String> filter(List<String> urlList, String artifactId, BuildType buildType)
        throws Exception {

        List<String> resultLinks = new ArrayList<String>();
        String latestVersion = null;
        String latestDate = null;
        for (String urlStr : urlList) {
            if (urlStr.indexOf("protocol") != -1 || urlStr.indexOf("format") != -1 ) continue;
            Artifact artifact = DownloadLinkUtils.parseDownloadLink(urlStr);
            if (artifact == null || artifact.getArtifactId() != null && artifactId != null
                    && !artifactId.equals(artifact.getArtifactId())) continue;

            // build type
            if (buildType != null && artifact.getBuildType() != null) {
                List<BuildType> values = Arrays.asList(BuildType.values());
                if (values.indexOf(artifact.getBuildType()) > values.indexOf(buildType)) continue;
            }

            if (artifact.getFileName() != null && !artifact.getFileName().endsWith(".zip")) continue;

            if (latestVersion == null || artifact.getVersion() != null && compareVersion(artifact.getVersion(), latestVersion) > 0) {
                latestVersion = artifact.getVersion();
            }
            if (latestDate == null || artifact.getBuildDate() != null && artifact.getBuildDate().compareTo(latestDate) > 0) {
                latestDate = artifact.getBuildDate();
            }

            resultLinks.add(urlStr);
        }

        // Some exceptions "-win32.zip" "&mirror_id=1"
        // or if no special urls found, select latest version or latest date links
        List<String> specialLinks = new ArrayList<String>();
        List<String> idVersionLinks = new ArrayList<String>();
        List<String> versionLinks = new ArrayList<String>();
        List<String> dateLinks = new ArrayList<String>();
        for (String url : resultLinks) {
            Artifact artifact = DownloadLinkUtils.parseDownloadLink(url);
            if (artifact.getFileName() != null && artifact.getFileName().endsWith("-win32.zip") || url.endsWith("&mirror_id=1")) {
                specialLinks.add(url);
            }
            if (artifact.getVersion() != null && artifact.getVersion().equals(latestVersion)) {
                versionLinks.add(url);
                if (url.contains(artifactId)) idVersionLinks.add(url);
            }
            if (artifact.getBuildDate() != null && artifact.getBuildDate().equals(latestDate)) {
                dateLinks.add(url);
            }
        }

        if (!specialLinks.isEmpty()) {
            resultLinks = specialLinks;
        } else if (!idVersionLinks.isEmpty()) {
            resultLinks = idVersionLinks;
        } else if (!versionLinks.isEmpty()) {
            resultLinks = versionLinks;
        } else if (!dateLinks.isEmpty()) {
            resultLinks = dateLinks;
        }

        return resultLinks;
    }
}
