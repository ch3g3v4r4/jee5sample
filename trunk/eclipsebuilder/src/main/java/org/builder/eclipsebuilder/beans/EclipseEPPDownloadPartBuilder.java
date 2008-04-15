package org.builder.eclipsebuilder.beans;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.builder.eclipsebuilder.beans.Configuration.BuildType;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EclipseEPPDownloadPartBuilder extends PartBuilderHelper {
    @Override
    protected String[] getDownloadAndChecksumLinks(String url,
            String artifactId, BuildType buildType) throws Exception {
        List<String> result = new ArrayList<String>();
        Element buildCell = getBuildCell(url, artifactId);
        URL baseUrl = new URL(url);
        for (Element aElem : getElementsByTagName(buildCell, "a")) {
            String link = new URL(baseUrl, aElem.getAttribute("href"))
                    .toExternalForm();
            if (link.toLowerCase().endsWith(".zip")) {
                result.add(link);
                result.add(link + ".md5");
                break;
            }
        }
        return result.toArray(new String[2]);
    }

    private List<Element> getElementsByTagName(Element parentElement,
            String tagName) {
        List<Element> result = new ArrayList<Element>();
        Stack<Element> stack = new Stack<Element>();
        stack.push(parentElement);
        while (!stack.isEmpty()) {
            Element element = stack.pop();
            if (element.getTagName().equalsIgnoreCase(tagName)) {
                result.add(element);
            }
            NodeList nodes = element.getChildNodes();
            for (int i = nodes.getLength() - 1; i >= 0; i--) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    stack.push((Element) node);
                }
            }
        }
        return result;
    }

    private Element getBuildCell(String url, String artifactId)
            throws Exception {

        Element buildCell = null;
        DOMParser parser = new DOMParser();
        parser.parse(url);
        Document document = parser.getDocument();
        NodeList tables = document.getElementsByTagName("table");
        Element table = (Element) tables.item(0);
        Element headerRow = null;
        List<Element> bodyRows = new ArrayList<Element>();
        NodeList nodes = table.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (headerRow == null) {
                    headerRow = (Element) node;
                } else {
                    bodyRows.add((Element) node);
                }
            }
        }
        int artifactIndex = findColumn(headerRow, artifactId);
        List<Element> artifactCells = getCells(bodyRows, artifactIndex);
        for (Element e : artifactCells) {
            if (e.getTextContent().toUpperCase().contains("SUCCESS")) {
                buildCell = e;
                break;
            }
        }
        return buildCell;
    }

    private List<Element> getCells(List<Element> bodyRows, int artifactIndex) {
        List<Element> cells = new ArrayList<Element>();

        for (int i = 0; i < bodyRows.size(); i++) {
            Element row = (Element) bodyRows.get(i);
            int cellIndex = 0;
            NodeList nodes = row.getChildNodes();
            for (int j = 0; j < nodes.getLength(); j++) {
                Node node = nodes.item(j);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    if (cellIndex == artifactIndex) {
                        Element cell = (Element) node;
                        cells.add(cell);
                    }
                    cellIndex++;
                }
            }
        }
        return cells;
    }

    private int findColumn(Element row, String artifactId) {
        int result = -1;
        NodeList nodes = row.getChildNodes();
        int cellIndex = 0;
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element column = (Element) node;
                String text = column.getTextContent();
                if (artifactId.trim().equalsIgnoreCase(text.trim())) {
                    result = cellIndex;
                    break;
                }
                cellIndex++;
            }
        }
        return result;
    }
}
