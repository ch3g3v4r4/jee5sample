package sample.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;

public class Import {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		InputStream is = null;

		List<Type1> table1 = new ArrayList<Type1>();
		List<Type2> table2 = new ArrayList<Type2>();
		Map<Object, Integer> lineInfo = new Hashtable<Object, Integer>();

		// Parse CSV file into row values
		parseCSV(is, table1, table2, lineInfo);

		// Insert row values to database
		insertDatabase(table1, table2, lineInfo);

	}

	private static void insertDatabase(List<Type1> table1,
			List<Type2> table2, Map<Object, Integer> lineInfo) {
		Connection con = null;
		try {
			// Insert rows
			insertTable1(table1, con, lineInfo);
			insertTable2(table2, con, lineInfo);

			//commit
		} catch (Exception e) {
			// rollback
		} finally {
			// Close connection
		}
	}

	private static void parseCSV(InputStream is, List<Type1> table1,
			List<Type2> table2, Map<Object, Integer> lineInfo)
			throws CSVImportException {
		CSVParser p = new CSVParser(new InputStreamReader(is), CSVStrategy.DEFAULT_STRATEGY);
		int lineNumber = 0;
		do {
			try {
				lineNumber++;
				String[] line = p.getLine();
				if (line == null) {
					break;
				}
				lineNumber = p.getLineNumber();
				String type = (line.length > 0 ? line[0] : null);
				if (Type1.TYPE.equalsIgnoreCase(type.trim())) {
	                Type1 o1 = new Type1(Arrays.copyOfRange(line, 1, line.length));
	                table1.add(o1);
	                lineInfo.put(o1, lineNumber);
				} else if (Type2.TYPE.equalsIgnoreCase(type.trim())) {
	                Type2 o2 = new Type2(Arrays.copyOfRange(line, 1, line.length));
	                table2.add(o2);
	                lineInfo.put(o2, lineNumber);
				} else {
					throw new CSVImportException(lineNumber, type, "Expected a datatype value.");
				}
			} catch (Exception e) {
				if (!(e instanceof CSVImportException)) {
					throw new CSVImportException(lineNumber, "Unknown error.", e);
				} else {
					throw (CSVImportException) e;
				}
			}

		} while (true);
	}

	private static void insertTable2(List<Type2> table, Connection con, Map<Object, Integer> lineInfo) {
		// TODO Auto-generated method stub
	}

	private static void insertTable1(List<Type1> table, Connection con, Map<Object, Integer> lineInfo) {
		// TODO Auto-generated method stub
	}

	private static class CSVImportException extends Exception {
		public CSVImportException(int line, String value, String error) {
			super("Invalid value: \"" + value + "\" found at line: " + line + ". Error:" + error);
		}
		public CSVImportException(int line, String error, Throwable t) {
			super("Error found at line: " + line + ". Error:" + error, t);
		}
	}
	private static class Type1 {
		public static final String TYPE = "TYPE1";
		public Type1(String[] values) {

		}
	}
	private static class Type2 {
		public static final String TYPE = "TYPE2";
		public Type2(String[] values) {

		}
	}

}
