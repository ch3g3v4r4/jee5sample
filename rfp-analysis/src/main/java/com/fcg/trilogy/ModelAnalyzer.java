package com.fcg.trilogy;

import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.fcg.trilogy.model.RFP;
import com.fcg.trilogy.model.RFPManager;

public class ModelAnalyzer {

	public static void analyze(RFPManager manager) throws Exception {
		Collection<RFP> all = manager.findAll();
		System.out.println("Total: " + all.size() );
		SortedSet<Date> dates = new TreeSet<Date>();
		SortedSet<String> serviceTypes = new TreeSet<String>();
		for (Iterator<RFP> it = all.iterator(); it.hasNext();) {
			RFP rfp = it.next();
			dates.add(rfp.getDate());
			serviceTypes.add(rfp.getServiceType());
		}

	    HSSFWorkbook wb = new HSSFWorkbook();
	    FileOutputStream fileOut = new FileOutputStream("rfp-analysis.xls");
	    HSSFSheet sheet = wb.createSheet("RFP Analysis");

	    // Date row
	    HSSFRow row = sheet.createRow((short)0);
	    HSSFCell cell = row.createCell((short)0);
	    cell.setCellValue("Date");
	    short i = 1;
	    for (Iterator<Date> it = dates.iterator(); it.hasNext();) {
			Date date = it.next();
		    HSSFCellStyle cellStyle = wb.createCellStyle();
		    cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
		    cell = row.createCell(i++);
		    cell.setCellValue(date);
		    cell.setCellStyle(cellStyle);

		}

	    short rowIndex = 1;

	    // Total row
	    row = sheet.createRow(rowIndex++);
	    cell = row.createCell((short)0);
	    cell.setCellValue("Total");
	    short colIndex = 1;
	    for (Iterator<Date> it = dates.iterator(); it.hasNext();) {
			Date date = it.next();
		    cell = row.createCell(colIndex++);
		    cell.setCellValue(countRFPWithDate(all, date));
		}

	    // Service types rows
	    for (Iterator<String> it = serviceTypes.iterator(); it.hasNext();) {
			String serviceType = it.next();
		    row = sheet.createRow(rowIndex++);
		    cell = row.createCell((short)0);
		    cell.setCellValue(serviceType);
		    colIndex = 1;
		    for (Iterator<Date> it2 = dates.iterator(); it2.hasNext();) {
				Date date = it2.next();
			    cell = row.createCell(colIndex++);
			    cell.setCellValue(countRFPWithDateAndType(all, date, serviceType));
			}
		}

	    wb.write(fileOut);
	    fileOut.close();

	    SortedSet<String> projectNames = new TreeSet<String>();
	    for (Iterator<RFP> it = all.iterator(); it.hasNext();) {
			RFP rfp = it.next();
			projectNames.add(rfp.getProjectName());
		}
	    System.out.println("++++++++++++++++++++++++++++++++++++");
	    for (Iterator<String> it = projectNames.iterator(); it.hasNext();) {
			String name = it.next();
			System.out.println(name);
		}
	}

	private static int countRFPWithDate(Collection<RFP> all, Date date) {
		int count = 0;
		for (Iterator it = all.iterator(); it.hasNext();) {
			RFP rfp = (RFP) it.next();
			if (date.equals(rfp.getDate())) {
				count ++;
			}
		}
		return count;
	}

	private static int countRFPWithDateAndType(Collection<RFP> all, Date date, String serviceType) {
		int count = 0;
		for (Iterator it = all.iterator(); it.hasNext();) {
			RFP rfp = (RFP) it.next();
			if (date.equals(rfp.getDate()) && serviceType.equals(rfp.getServiceType())) {
				count ++;
			}
		}
		return count;
	}
}
