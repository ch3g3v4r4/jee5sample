package com.fcg.trilogy;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;
import org.xml.sax.InputSource;

import com.fcg.trilogy.model.RFP;
import com.fcg.trilogy.model.RFPManager;

/**
 * Class to load RFP information into RFPManager object.
 *
 * @author tha
 *
 */
public class InputLoader {

	private static Logger logger = Logger.getLogger(InputLoader.class);

	public static void load(File inputDirectory, RFPManager rfpManager)
			throws Exception {
		logger.info("Processing directory " + inputDirectory.getAbsolutePath());
		// Get list of RFP zip files
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.matches("\\d{4}-\\d{2}-\\d{2}\\.zip");
			}
		};
		String[] zipFiles = inputDirectory.list(filter);
		Arrays.sort(zipFiles);

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 0; i < zipFiles.length; i++) {
			File zipFileObject = new File(inputDirectory, zipFiles[i]);
			String dateString = zipFiles[i].substring(0, zipFiles[i].indexOf('.'));
			String indexFilePath = dateString + "/index.html";
			ZipFile zipFile = new ZipFile(zipFileObject);
	        Date date = (Date)formatter.parse(dateString);

			Enumeration entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				if (entry.getName().equals(indexFilePath)) {
					logger.info("Begin processing " + indexFilePath);
					InputStream is = zipFile.getInputStream(entry);
					load(date, is, rfpManager);
					logger.info("End processing " + indexFilePath);
				}
			}
			zipFile.close();
		}
		logger.info("End processing directory " + inputDirectory.getAbsolutePath());

	}

	protected static void load(Date date, InputStream is, RFPManager rfpManager)
		throws Exception {
		String[][] htmlContent = parseHTMLFile(is);
		Map<String, String> column2Property = new Hashtable <String, String>();
		column2Property.put("S.No", "");
		column2Property.put("ID", "projectId");
		column2Property.put("Project", "projectName");
		column2Property.put("Owner", "owner");
		column2Property.put("Service Type", "serviceType");
		column2Property.put("Duration", "duration");
		column2Property.put("RFP", "");
		column2Property.put("Subversion Path", "");

		for (int i = 1; i < htmlContent.length; i++) {
			String[] row = htmlContent[i];
			RFP rfp = new RFP();
			rfp.setDate(date);
			for (int j = 0; j < row.length; j++) {
				String header = htmlContent[0][j];
				String property = column2Property.get(header);
				if (property != null && !property.equals("")) {
					String value = row[j];
					BeanUtils.setProperty(rfp, property, value);
				}
			}
			logger.debug("Inserting to DB: " + rfp.getProjectName());
			rfpManager.persist(rfp);
		}
	}

	protected static String[][] parseHTMLFile(InputStream ist) throws Exception {
		logger.debug("Begin parsing index.html ...");

		DOMParser parser = new DOMParser();
		InputSource is = new InputSource(ist);
		is.setEncoding("UTF-8");
		parser.parse(is);
		HTMLDocument html = (HTMLDocument) parser.getDocument();
		HTMLElement body = html.getBody();
		NodeList tableElems = body.getElementsByTagName("table");
		Element tableElem = (Element) tableElems.item(0);
		NodeList trElems = tableElem.getElementsByTagName("tr");

		// Parse header row
		List<String> columnNames = new ArrayList<String>();
		Element trElem = (Element) trElems.item(0);
		NodeList thElems = trElem.getElementsByTagName("th");
		for (int i = 0; i < thElems.getLength(); i++) {
			String columnName = nodeToString(thElems.item(i));
			columnNames.add(columnName);
		}

		// Parse data rows
		List<List<String>> data = new ArrayList<List<String>>();
		for (int i = 1; i < trElems.getLength(); i++) {
			List<String> dataRow = new ArrayList<String>();
			trElem = (Element) trElems.item(i);
			NodeList tdElems = trElem.getElementsByTagName("td");
			for (int j = 0; j < tdElems.getLength(); j++) {
				String dataCell = nodeToString(tdElems.item(j));
				dataRow.add(dataCell);
			}
			data.add(dataRow);
		}

		String[][] result = new String[1 + data.size()][columnNames.size()];

		// Fill header names to result array
		for (int i = 0; i < columnNames.size(); i++) {
			result[0][i] = columnNames.get(i);
		}

		// Fill data to result array
		for (int i = 0; i < data.size(); i++) {
			List<String> dataRow = data.get(i);
			for (int j = 0; j < dataRow.size(); j++) {
				result[i+1][j] = dataRow.get(j);
			}
		}

		logger.debug("End parsing index.html ...");
		return result;
	}

	protected static String nodeToString(Node aNode) {
		String result = "";
		LinkedList<Node> stack = new LinkedList<Node>();
		stack.addFirst(aNode);
		while (!stack.isEmpty()) {
			Node node = (Node) stack.removeFirst();
			if (node.getNodeType() == Node.TEXT_NODE) {
				if (result.length() > 0) result += " ";
				result += trim(node.getNodeValue());
			} else if (node.getNodeType() == Node.ELEMENT_NODE) {
				NodeList children = node.getChildNodes();
				for (int i = children.getLength() - 1; i >= 0; i--) {
					stack.addFirst(children.item(i));
				}
			}
		}
		return result;
	}

	/**
	 * Checks if a string is empty (contains only commas and/or whitespaces).
	 *
	 * @param str
	 *            string to check
	 * @return true if it's a empty line, otherwise returns false
	 */
	protected static boolean isEmptyString(String str) {
		boolean empty = true;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (!isWhitespace(c)) {
				empty = false;
			}
		}
		return empty;
	}

	/**
	 * This method works like String.trim() method, but it also removes #160
	 * characters.
	 *
	 * @param str
	 *            input
	 * @return result
	 */
	protected static String trim(String str) {
		String result = str;
		if (str != null && str.length() > 0) {
			int i = 0;
			while (i < str.length() && isWhitespace(str.charAt(i))) {
				i++;
			}
			result = "";
			if (i < str.length() && !isWhitespace(str.charAt(i))) {
				int j = str.length() - 1;
				while (isWhitespace(str.charAt(j))) {
					j--;
				}
				if (j > 0 && !isWhitespace(str.charAt(j))) {
					result = str.substring(i, j + 1);
				}
			}
		}
		return result;
	}

	/**
	 * Checks if a character is a whitespace character (non-break-space chars
	 * are also treat as whitespace)
	 *
	 * @param c
	 *            character to check
	 * @return true if it's a white space
	 */
	private static boolean isWhitespace(char c) {
		return Character.isWhitespace(c) || c == 160;
	}

}
