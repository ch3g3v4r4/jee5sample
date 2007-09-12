package com.fcgv.mojo.checkreport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.fcgv.mojo.checkreport.ChangeLogUtil.StringDate;
import com.fcgv.mojo.checkreport.xmltypes.BugCollection;
import com.fcgv.mojo.checkreport.xmltypes.BugInstance;
import com.fcgv.mojo.checkreport.xmltypes.CPDFileType;
import com.fcgv.mojo.checkreport.xmltypes.CheckstyleType;
import com.fcgv.mojo.checkreport.xmltypes.Duplication;
import com.fcgv.mojo.checkreport.xmltypes.DuplicationType;
import com.fcgv.mojo.checkreport.xmltypes.FileType;
import com.fcgv.mojo.checkreport.xmltypes.PmdCpdType;

public class CheckReportBean {
	
	private File checkstyleReportFile; // Checkstyle report file (XML)
	private File findbugsReportFile; // FindBugs report file (XML)
	private File pmdReportFile; // PMD report file (XML)
	private File CPDReportFile; // CPD report file (XML)
	private Map<String, StringDate> scmPath2Author; // 
	private int nDays;
	private File outputXLS; // Excel report file (output file)
	private File outputStatisticXLS; // Excel statistic report file (output file)

	public File getCheckstyleReportFile() {
		return checkstyleReportFile;
	}

	public void setCheckstyleReportFile(File checkstyleReportFile) {
		this.checkstyleReportFile = checkstyleReportFile;
	}

	public File getFindbugsReportFile() {
		return findbugsReportFile;
	}

	public void setFindbugsReportFile(File findbugsReportFile) {
		this.findbugsReportFile = findbugsReportFile;
	}

	public File getPmdReportFile() {
		return pmdReportFile;
	}

	public void setPmdReportFile(File pmdReportFile) {
		this.pmdReportFile = pmdReportFile;
	}

	public File getCPDReportFile() {
		return CPDReportFile;
	}

	public void setCPDReportFile(File reportFile) {
		CPDReportFile = reportFile;
	}

	public Map<String, StringDate> getScmPath2Author() {
		return scmPath2Author;
	}

	public void setScmPath2Author(Map<String, StringDate> scmPath2Author) {
		this.scmPath2Author = scmPath2Author;
	}

	public int getNDays() {
		return nDays;
	}

	public void setNDays(int days) {
		nDays = days;
	}

	public File getOutputXLS() {
		return outputXLS;
	}

	public void setOutputXLS(File outputXLS) {
		this.outputXLS = outputXLS;
	}

	public File getOutputStatisticXLS() {
		return outputStatisticXLS;
	}

	public void setOutputStatisticXLS(File outputStatisticXLS) {
		this.outputStatisticXLS = outputStatisticXLS;
	}

	public void generate() {
		try {
			Map<String, ReportFileItem> file2Errors = buildFile2ErrorMap();
			
			file2Errors = filterByAfterDate(file2Errors, nDays);

			buildXLSReport(file2Errors, outputXLS);
			
			buildXLSStatisticReport(file2Errors, outputStatisticXLS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Map<String, ReportFileItem> filterByAfterDate(
			Map<String, ReportFileItem> file2Errors, int days) {
		Map<String, ReportFileItem> result;
		if (days == 0) {
			result = file2Errors;
		} else {
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.DAY_OF_MONTH, -1 * days);
			result = new HashMap<String, ReportFileItem>();
			for (String file : file2Errors.keySet()) {
				ReportFileItem item = file2Errors.get(file);
				Date itemDate = item.getDate();
				if (itemDate == null || itemDate.getTime() > cal.getTimeInMillis()) {
					result.put(file, item);
				}
			}
		}
		return result;
	}

	private void buildXLSStatisticReport(
			Map<String, ReportFileItem> file2Errors, 
			File outputXLS) throws IOException {

	    HSSFWorkbook wb = new HSSFWorkbook();
	    HSSFSheet sheet = wb.createSheet("Statistic Report");
	    short rowNum = 0;
	    short colNum = 0;
	    
	    // AUTHOR MESSAGES OCCURS 	    
	    // Create a row and put some cells in it. Rows are 0 based.
	    HSSFRow row = sheet.createRow(rowNum++);
	    HSSFCellStyle style = wb.createCellStyle();
	    
	    sheet.setColumnWidth(colNum, (short) (15*256));
	    HSSFCell cell = row.createCell(colNum++);
	    cell.setCellValue("Author");
	    cell.setCellStyle(style);
	    
	    sheet.setColumnWidth(colNum, (short) (45*256));
	    cell = row.createCell(colNum++);
	    cell.setCellValue("Message");
	    cell.setCellStyle(style);
	    
	    sheet.setColumnWidth(colNum, (short) (30*256));
	    cell = row.createCell(colNum++);
	    cell.setCellValue("Occurs");
	    cell.setCellStyle(style);
	    
	    Set<String> authors = new HashSet<String>();
	    for (String file : file2Errors.keySet()) {
	    	authors.add(file2Errors.get(file).getAuthor());
	    }
	    
	    for (String author : authors) {
	    	Map<String, Integer> messages2Occurs = buildMessage2OccursMap(author, file2Errors);
	    	for (String message : messages2Occurs.keySet()) {
	    		colNum = 0;
	    	    row = sheet.createRow(rowNum++);
	    	    cell = row.createCell(colNum++);
	    	    cell.setCellValue(author);
	    	    cell = row.createCell(colNum++);
	    	    cell.setCellValue(message);
	    	    cell = row.createCell(colNum++);
	    	    cell.setCellValue(messages2Occurs.get(message).intValue());
	    	}
	    }

	    // Write the output to a file
	    FileOutputStream fileOut = new FileOutputStream(outputXLS);
	    wb.write(fileOut);
	    fileOut.close();
	}

	private Map<String, Integer> buildMessage2OccursMap(String author,
			Map<String, ReportFileItem> file2Errors) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		// Filter the files of others
		Map<String, ReportFileItem> file2ErrorsForAuthor = new HashMap<String, ReportFileItem>(); 
		for (String file : file2Errors.keySet()) {
			ReportFileItem fileItem = file2Errors.get(file);
			if (author.equals(fileItem.getAuthor())) file2ErrorsForAuthor.put(file, fileItem);
		}
		
		// Build an initial report
		Map<String, Integer> message2Occurs = new HashMap<String, Integer>();
		for (String file : file2ErrorsForAuthor.keySet()) {
			ReportFileItem fileItem = file2ErrorsForAuthor.get(file);
			for (ReportFileItemMessage message : fileItem.getMessages()) {
				String msg = message.getMessage();
				Integer occurs;
				if (message2Occurs.containsKey(msg)) {
					occurs = message2Occurs.get(msg).intValue() + 1;
				} else {
					occurs = new Integer(1);
				}
				message2Occurs.put(msg, occurs);
			}
		}
		
		// messages occurs > 1 will be in final result, messages occurs=1 should be merged to others
		Set<String> messageOccurs1 = new HashSet<String>();
		for (String msg : message2Occurs.keySet()) {
			Integer occurs = message2Occurs.get(msg);
			if (occurs.intValue() > 2) {
				result.put(msg, occurs);
			} else {
				messageOccurs1.add(msg);
			}
		}
		
		// messages occurs=1 should be merged to others
		do {
			String group = selectAGroupWithMostMembers(messageOccurs1);
			if (group == null) break;
			Set<String> members = selectMembersOfGroup(group, messageOccurs1);
			messageOccurs1.removeAll(members); // for next iteration
			result.put(members.iterator().next(), members.size());
		} while (true);
		
		return result;
	}

	private static String selectAGroupWithMostMembers(Set<String> messages) {
		String result;
		// Group messages using first 12 chars
		Set<String> prefixsOrSuffixs = new HashSet<String>();
		for (String msg : messages) {
			if (msg.length() >= 12) {
				prefixsOrSuffixs.add(msg.substring(0, 12));
				prefixsOrSuffixs.add(msg.substring(msg.length() - 12));
			}
		}
		Map<String, Integer> group2Occurs = new HashMap<String, Integer>(); 
		for (String msg : messages) {
			for (String group : prefixsOrSuffixs) {
				if (msg.startsWith(group) || msg.endsWith(group)) {
					if (group2Occurs.containsKey(group)) {
						group2Occurs.put(group, group2Occurs.get(group) + 1);
					} else {
						group2Occurs.put(group, 1);
					}
				}
			}
		}
		Integer maxNumber = new Integer(0);
		String selectedGroup = null;
		for (String group : group2Occurs.keySet()) {
			Integer occurs = group2Occurs.get(group);
			if (occurs.compareTo(maxNumber) > 0) {
				maxNumber = occurs;
				selectedGroup = group;
			}
		}
		result = selectedGroup;
		return result;
	}
	
	private static Set<String> selectMembersOfGroup(String group, Set<String> messages) {
		Set<String> result = new HashSet<String>();
		for (String msg : messages) {
			if (msg.startsWith(group) || msg.endsWith(group)) {
				result.add(msg);
			}
		}
		return result;
	}
	
	private void buildXLSReport(
			Map<String, ReportFileItem> file2Errors,
			File outputXLS) throws IOException {

	    HSSFWorkbook wb = new HSSFWorkbook();
	    HSSFSheet sheet = wb.createSheet("Report");
	    short rowNum = 0;
	    short colNum = 0;
	    
	    // AUTHOR FILENAME LINENUMBER MESSAGE SCMPATH FILEPATH 	    
	    // Create a row and put some cells in it. Rows are 0 based.
	    HSSFRow row = sheet.createRow(rowNum++);
	    HSSFCellStyle style = wb.createCellStyle();
	    
	    sheet.setColumnWidth(colNum, (short) (15*256));
	    HSSFCell cell = row.createCell(colNum++);
	    cell.setCellValue("Author");
	    cell.setCellStyle(style);
	    
	    sheet.setColumnWidth(colNum, (short) (30*256));
	    cell = row.createCell(colNum++);
	    cell.setCellValue("File");
	    cell.setCellStyle(style);
	    
	    sheet.setColumnWidth(colNum, (short) (10*256));
	    cell = row.createCell(colNum++);
	    cell.setCellValue("Line");
	    cell.setCellStyle(style);
	    
	    sheet.setColumnWidth(colNum, (short) (45*256));
	    cell = row.createCell(colNum++);
	    cell.setCellValue("Message");
	    cell.setCellStyle(style);
	    
	    sheet.setColumnWidth(colNum, (short) (30*256));
	    cell = row.createCell(colNum++);
	    cell.setCellValue("SCM Path");
	    cell.setCellStyle(style);
	    
	    cell = row.createCell(colNum++);
	    cell.setCellValue("File Path");
	    cell.setCellStyle(style);

    	for (String filePath : file2Errors.keySet()) {
	    	ReportFileItem errors = file2Errors.get(filePath);
	    	String scmPath = errors.getScmPath();
	    	String author = errors.getAuthor();
	    	String fileName = errors.getFileName();
	    	for (ReportFileItemMessage message : errors.getMessages()) {
	    		colNum = 0;
	    	    row = sheet.createRow(rowNum++);
	    	    cell = row.createCell(colNum++);
	    	    cell.setCellValue(author);
	    	    cell = row.createCell(colNum++);
	    	    cell.setCellValue(fileName);
	    	    cell = row.createCell(colNum++);
	    	    try {
	    	    	cell.setCellValue(Integer.parseInt(message.getLine()));
	    	    } catch (Exception e) {
	    	    	cell.setCellValue(message.getLine());
				}
	    	    
	    	    cell = row.createCell(colNum++);
	    	    cell.setCellValue(message.getMessage());
	    	    cell = row.createCell(colNum++);
	    	    cell.setCellValue(scmPath);	    		
	    	    cell = row.createCell(colNum++);
	    	    cell.setCellValue(filePath);
	    	}
	    }

	    // Write the output to a file
	    FileOutputStream fileOut = new FileOutputStream(outputXLS);
	    wb.write(fileOut);
	    fileOut.close();
	}

	private String guessSCMPath(String filePath,
			Set<String> scmPaths) {
		int matchedLength = 0;
		String matchedScmPath = null;
		for (String scmPath : scmPaths) {
			// calculate the matched characters
			if (scmPath != null && filePath != null && scmPath.length() > 0 && filePath.length() > 0) {
				int index1 = scmPath.length() - 1;
				int index2 = filePath.length() - 1;
				int matchedCharNum = 0;
				while (index1 >= 0 && index2 >= 0){
					char c1 = scmPath.charAt(index1);
					char c2 = filePath.charAt(index2);
					boolean matched;
					if (c1 == c2) {
						matched = true;
					} else {
						matched = "/\\".indexOf(c1) >= 0 && "/\\".indexOf(c2) >= 0;
					}
					if (matched) {
						matchedCharNum ++;
						index1 --;
						index2 --;
					} else {
						break;
					}
				}
				if (matchedCharNum > matchedLength) {
					char lastMatchedChar1 = scmPath.charAt(index1 + 1);
					char lastMatchedChar2 = filePath.charAt(index2 + 1);
					if (index1 == -1 || index2 == -1 
							|| "/\\".indexOf(lastMatchedChar1) >= 0 && "/\\".indexOf(lastMatchedChar2) >= 0) {
						matchedLength = matchedCharNum;
						matchedScmPath = scmPath;
					}
				}
			}
		}
		return matchedScmPath;
	}



	private Map<String, ReportFileItem> buildFile2ErrorMap() throws JAXBException, IOException {
		
		Map<String, ReportFileItem> fileName2Error = new Hashtable<String, ReportFileItem>();

		// process checkstyleReportFile
		if (checkstyleReportFile.exists()) {
			Map<String, ReportFileItem> fileName2Error1 = processReportCheckstyleType(checkstyleReportFile);
			fileName2Error = merge(fileName2Error, fileName2Error1);
		}
		
		// process pmdReportFile
		if (pmdReportFile.exists()) {
			Map<String, ReportFileItem> fileName2Error2 = processReportCheckstyleType(pmdReportFile);
			fileName2Error = merge(fileName2Error, fileName2Error2);
		}
		
		// process CPDReportFile
		if (CPDReportFile.exists()) {
			Map<String, ReportFileItem> fileName2Error2 = processReportCPDType(CPDReportFile);
			fileName2Error = merge(fileName2Error, fileName2Error2);
		}
		
		// process findbugsReportFile
		if (findbugsReportFile.exists()) {
			Map<String, ReportFileItem> fileName2Error3 = processFindBugsReportType(findbugsReportFile);
			fileName2Error = merge(fileName2Error, fileName2Error3);
		}

	    for (String filePath : fileName2Error.keySet()) {
	    	ReportFileItem errors = fileName2Error.get(filePath);
	    	String scmPath = guessSCMPath(filePath, scmPath2Author.keySet());
	    	errors.setScmPath(scmPath);
	    	String author;
	    	Date date;
	    	if (scmPath != null) {
	    		StringDate sd = scmPath2Author.get(scmPath); 
	    		author = sd.getString();
	    		date = sd.getDate();
	    	} else {
	    		author = "";
	    		date = null;
	    	}
	    	errors.setAuthor(author);
	    	errors.setDate(date);
	    	String uniFilePath = filePath.replaceAll("\\\\", "/");
	    	errors.setFileName(uniFilePath.substring(uniFilePath.lastIndexOf('/') + 1));
	    }
		return fileName2Error;
	}

	private static Map<String, ReportFileItem> processReportCPDType(File cpdReportFile) throws JAXBException {
		Map<String, ReportFileItem> result = new Hashtable<String, ReportFileItem>();
		JAXBContext jc = JAXBContext.newInstance("com.fcgv.mojo.checkreport.xmltypes", CheckReportBean.class.getClassLoader());
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		PmdCpdType pmdCpd = (PmdCpdType) unmarshaller.unmarshal(cpdReportFile);
		for (Object duplicationObject : pmdCpd.getDuplication()) {
			Duplication duplication = (Duplication) duplicationObject; 
			for (Object fileObject : duplication.getFile()) {
				CPDFileType file = (CPDFileType) fileObject;
				ReportFileItem reportFileItem = convertToReportFileItem(duplication, file); 
				String fileName = reportFileItem.getName();
				if (!result.containsKey(fileName)) {
					result.put(fileName, reportFileItem);
				} else {
					ReportFileItem existingReportFileItem = result.get(fileName);
					result.put(fileName, mergeReportFileItems(reportFileItem, existingReportFileItem));
				}
			}
		}
		return result;

	}

	private static ReportFileItem convertToReportFileItem(DuplicationType duplication, CPDFileType file) {
		ReportFileItem result = new ReportFileItem();
		result.setName(file.getPath());
		ReportFileItemMessage message = new ReportFileItemMessage();
		message.setLine(file.getLine().toString());
		message.setMessage("Duplication found with " + duplication.getLines() + " lines.");
		List<ReportFileItemMessage> messages = new ArrayList<ReportFileItemMessage>();
		messages.add(message);
		result.setMessages(messages);
		return result;
	}

	private static Map<String, ReportFileItem> merge(
			Map<String, ReportFileItem> fileName2Error1,
			Map<String, ReportFileItem> fileName2Error2) {
		Map<String, ReportFileItem> result = new Hashtable<String, ReportFileItem>();
		Set<String> keys = new HashSet<String>();
		keys.addAll(fileName2Error1.keySet());
		keys.addAll(fileName2Error2.keySet());
		for (String key : keys) {
			ReportFileItem file1 = fileName2Error1.get(key);
			ReportFileItem file2 = fileName2Error2.get(key);
			if (file1 != null && file2 != null) {
				ReportFileItem file = new ReportFileItem();
				file.setName(key);
				List<ReportFileItemMessage> messages = new ArrayList<ReportFileItemMessage>();
				messages.addAll(file1.getMessages());
				messages.addAll(file2.getMessages());
				file.setMessages(messages);
				result.put(key, file);
			} else if (file1 != null) {
				result.put(key, file1);
			} else if (file2 != null) {
				result.put(key, file2);
			}
		}
		return result;
	}

	/**
	 * Process FindBugs-like XML output files.
	 * 
	 * @param findbugsReportFile
	 * @return
	 * @throws JAXBException
	 * @throws IOException 
	 */
	private static Map<String, ReportFileItem> processFindBugsReportType(File findbugsReportFile) throws JAXBException, IOException {
		Map<String, ReportFileItem> result = new Hashtable<String, ReportFileItem>();
		JAXBContext jc = JAXBContext.newInstance("com.fcgv.mojo.checkreport.xmltypes", CheckReportBean.class.getClassLoader());
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		fixErrorsInFindBugsReport(findbugsReportFile);
		BugCollection findbugs = (BugCollection) unmarshaller.unmarshal(findbugsReportFile);
		
		for (Object fileObject : findbugs.getFile()) {
			FileType file = (FileType) fileObject;
			ReportFileItem reportFileItem = convertToReportFileItem(file); 
			String relativeFilePath = reportFileItem.getName();
			if (!result.containsKey(relativeFilePath)) {
				result.put(relativeFilePath, reportFileItem);
			} else {
				ReportFileItem existingReportFileItem = result.get(relativeFilePath);
				result.put(relativeFilePath, mergeReportFileItems(reportFileItem, existingReportFileItem));
			}
		}
		return result;
	}


	private static void fixErrorsInFindBugsReport(File findbugsReportFile) throws IOException {
		// Replace all occurs <init> with &lt;init&gt;
		File findbugsReportFolder = findbugsReportFile.getParentFile();
		File newFile = File.createTempFile("temp", ".tmp", findbugsReportFolder);
		FileWriter writer = new FileWriter(newFile);
        FileReader isr = new FileReader(findbugsReportFile);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        boolean changed = false;
        while ( (line = br.readLine()) != null) {
            if (line.indexOf("<init>") != -1) {
            	line = line.replaceAll("\\<init\\>", "&lt;init&gt;");
            	changed = true;
            }
            writer.write(line + "\n");
        }
        writer.close();
        br.close();
        isr.close();
        if (changed) {
	        findbugsReportFile.delete();
	        newFile.renameTo(findbugsReportFile);
        } else {
        	newFile.delete();
        }
	}

	private static String getRelativePath(String className) {
		String fileName;
		int lastPeriod = className.lastIndexOf('.');
		if (lastPeriod >= 0) {
			fileName = className.substring(lastPeriod + 1);
		} else {
			fileName = className;
		}
		int indexOf$ = fileName.indexOf('$');
		if (indexOf$ >= 0) {
			fileName = fileName.substring(0, indexOf$);
		}
		String filePath;
		if (lastPeriod >= 0) {
			String packageName = className.substring(0, lastPeriod);
			filePath = packageName + "." + fileName;
		} else {
			filePath = fileName;
		}
		filePath = filePath.replace('.', '/') + ".java";
		
		return filePath;
	}

	/**
	 * Process Checstyle-like XML output files.
	 * 
	 * @param checkstyleReportFile
	 * @return
	 * @throws JAXBException
	 */
	private static Map<String, ReportFileItem> processReportCheckstyleType(File checkstyleReportFile) throws JAXBException {
		Map<String, ReportFileItem> result = new Hashtable<String, ReportFileItem>();
		JAXBContext jc = JAXBContext.newInstance("com.fcgv.mojo.checkreport.xmltypes", CheckReportBean.class.getClassLoader());
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		CheckstyleType checkStyle = (CheckstyleType) unmarshaller.unmarshal(checkstyleReportFile);
		for (Object fileObject : checkStyle.getFile()) {
			CheckstyleType.FileType file = (CheckstyleType.FileType) fileObject;
			ReportFileItem reportFileItem = convertToReportFileItem(file); 
			String fileName = reportFileItem.getName();
			if (!result.containsKey(fileName)) {
				result.put(fileName, reportFileItem);
			} else {
				ReportFileItem existingReportFileItem = result.get(fileName);
				result.put(fileName, mergeReportFileItems(reportFileItem, existingReportFileItem));
			}
		}
		return result;
	}

	/**
	 * Merges two ReportFileItem objects.
	 * 
	 * @param reportFileItem1
	 * @param reportFileItem2
	 * @return
	 */
	private static ReportFileItem mergeReportFileItems(
			ReportFileItem reportFileItem1, ReportFileItem reportFileItem2) {
		ReportFileItem result = new ReportFileItem();
		result.setName(reportFileItem1.getName());
		List<ReportFileItemMessage> messages = new ArrayList<ReportFileItemMessage>();
		if (reportFileItem1.getMessages() != null && !reportFileItem1.getMessages().isEmpty()) {
			messages.addAll(reportFileItem1.getMessages());
		}
		if (reportFileItem2.getMessages() != null && !reportFileItem2.getMessages().isEmpty()) {
			messages.addAll(reportFileItem2.getMessages());
		}
		result.setMessages(messages);
		return result;
	}

	/**
	 * Converts object format from CheckstyleType.FileType to ReportFileItem.
	 * 
	 * @param file
	 * @return
	 */
	private static ReportFileItem convertToReportFileItem(CheckstyleType.FileType file) {
		ReportFileItem result = new ReportFileItem();
		result.setName(file.getName());
		List<ReportFileItemMessage> messages = new ArrayList<ReportFileItemMessage>(); 
		for (Object errorObject : file.getError()) {
			CheckstyleType.FileType.ErrorType error = (CheckstyleType.FileType.ErrorType) errorObject;
			ReportFileItemMessage message = new ReportFileItemMessage();
			message.setColumn(error.getColumn());
			message.setLine(error.getLine());
			message.setMessage(error.getMessage());
			messages.add(message);
		}
		for (Object violationObject : file.getViolation()) {
			CheckstyleType.FileType.ViolationType violation = (CheckstyleType.FileType.ViolationType) violationObject;
			ReportFileItemMessage message = new ReportFileItemMessage();
			message.setLine(violation.getLine());
			message.setMessage(violation.getValue());
			messages.add(message);
		}
		result.setMessages(messages);
		return result;
	}
	

	private static ReportFileItem convertToReportFileItem(FileType file) {
		ReportFileItem result = new ReportFileItem();
		
		String className = file.getClassname();
		String relativeFilePath = getRelativePath(className);
		result.setName(relativeFilePath);
		
		List<ReportFileItemMessage> messages = new ArrayList<ReportFileItemMessage>(); 
		for (Object bugInstanceObject : file.getBugInstance()) {
			BugInstance bugInstance = (BugInstance) bugInstanceObject;
			ReportFileItemMessage message = new ReportFileItemMessage();
			message.setLine(bugInstance.getLineNumber());
			message.setMessage(bugInstance.getMessage());
			messages.add(message);
		}
		result.setMessages(messages);
		
		return result;
	}

}
