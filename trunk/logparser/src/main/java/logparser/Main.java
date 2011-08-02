package logparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Day;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class Main {

	public static class LogHistory {
		public Map<Date, Long> hits = new HashMap<Date, Long>();
	}

	public static void main(String[] args) throws Exception {

		String path = "D:\\projects\\jee5sample\\logparser\\log";
		File folder = new File(path);

		String[] names = folder.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.contains(".log");
			}
		});
		Map<String, LogHistory> siteLogs = new HashMap<String, LogHistory>();
		for (String name : names) {
			String site = name.substring(0, name.indexOf(".log"));
			LogHistory log = siteLogs.get(site);
			if (log == null) {
				log = new LogHistory();
				siteLogs.put(site, log);
			}
			parse(new File(folder, name), log);
		}

		//XYDataset dataSet = createDataset(siteLogs);
		XYDataset dataSet = createDataset1(siteLogs);
		JFreeChart chart = createChart(dataSet);
		ChartUtilities.saveChartAsPNG(new File(path, "chart.png"), chart, 1024,
				768);
	}

	private static JFreeChart createChart(XYDataset dataset) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Web Hits",
				"Date", "Hits", dataset, true, true, false);

		return chart;

	}
    private static XYDataset createDataset1(Map<String, LogHistory> siteLogs) {
		Date min = null;
		Date max = null;
		List<TimeSeries> seriesList = new ArrayList<TimeSeries>();
		for (String name : siteLogs.keySet()) {
			seriesList.add(new TimeSeries(name, Minute.class));
			LogHistory history = siteLogs.get(name);
			for (Date date : history.hits.keySet()) {
				if (min == null || min.compareTo(date) > 0) {
					min = date;
				}
				if (max == null || max.compareTo(date) < 0) {
					max = date;
				}
			}
		}
		int x = 0;
		GregorianCalendar mincal = new GregorianCalendar();
		mincal.setTime(min);
		GregorianCalendar maxcal = new GregorianCalendar();
		maxcal.setTime(max);

        Minute current = new Minute(mincal.get(Calendar.MINUTE), mincal.get(Calendar.HOUR), mincal.get(Calendar.DAY_OF_MONTH), mincal.get(Calendar.MONTH)+1, mincal.get(Calendar.YEAR));
        long totalMins = (max.getTime() - min.getTime()) / 60 / 1000;
        for (long i = 0; i < totalMins; i++) {
			int j = 0;
			for (String name : siteLogs.keySet()) {
				LogHistory history = siteLogs.get(name);
				TimeSeries series = seriesList.get(j++);
				Long hits = history.hits.get(mincal.getTime());
				if (hits == null)
					hits = 0L;
				if (hits > 0)
					series.add(current, new Double(hits));
			}
            current = (Minute) current.next();
            mincal.add(Calendar.MINUTE, 1);
        }

		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		for (TimeSeries series : seriesList) {
			dataset.addSeries(series);
		}

		return dataset;
    }
	private static TimeSeriesCollection createDataset(
			Map<String, LogHistory> siteLogs) {
		Date min = null;
		Date max = null;

		List<TimeSeries> seriesList = new ArrayList<TimeSeries>();
		for (String name : siteLogs.keySet()) {
			seriesList.add(new TimeSeries(name, Minute.class));
			LogHistory history = siteLogs.get(name);
			for (Date date : history.hits.keySet()) {
				if (min == null || min.compareTo(date) > 0) {
					min = date;
				}
				if (max == null || max.compareTo(date) < 0) {
					max = date;
				}
			}
		}

		int x = 0;
		GregorianCalendar mincal = new GregorianCalendar();
		mincal.setTime(min);
		GregorianCalendar maxcal = new GregorianCalendar();
		maxcal.setTime(max);

		while (maxcal.after(mincal)) {

			int i = 0;
			for (String name : siteLogs.keySet()) {
				LogHistory history = siteLogs.get(name);
				TimeSeries series = seriesList.get(i++);
				Long hits = history.hits.get(mincal.getTime());
				if (hits == null)
					hits = 0L;
				series.add(new Minute((Date) (mincal.getTime().clone())), hits);
			}

			mincal.add(Calendar.MINUTE, 1);
			x++;

		}

		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		for (TimeSeries series : seriesList) {
			dataset.addSeries(series);
		}

		return dataset;

	}

	private static void parse(File file, LogHistory log) throws Exception {
		FileSystemManager fsManager = VFS.getManager();
		FileObject vfsfile;
		if (file.getName().endsWith(".gz")) {
			vfsfile = fsManager.resolveFile("gz:"
					+ file.toURI().toString()
					+ "!"
					+ file.getName().substring(0,
							file.getName().lastIndexOf(".gz")));
		} else {
			vfsfile = fsManager.resolveFile(file.toURI().toString());
		}
		InputStream is = vfsfile.getContent().getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		Pattern pattern = Pattern.compile("\\[(.+)\\]\\s+\"(.+)\"");
		DateTimeFormatter parser1 = ISODateTimeFormat.dateTimeParser();
		DateTimeFormatter parser2 = DateTimeFormat
				.forPattern("dd/MMM/yyyy:HH:mm:ss Z");

		do {
			String line = in.readLine();
			if (line == null)
				break;
			Date date = null;
			try {
				// 2011-08-01T00:00:20-04:00 ERR (3): ...[Error message: Too
				// many connections]
				DateTime dateTime = parser1.parseDateTime(line.substring(0,
						line.indexOf(' ')));
				if (line.indexOf(" ERR ") != -1) {
					date = dateTime.toDate();
				}
			} catch (Exception e) {
				// ignore
			}
			if (date == null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					// [24/Jul/2011:18:18:37 -0400] "GET / HTTP/1.1"
					String dateStr = matcher.group(1); // "24/Jul/2011:18:18:37 -0400"
					try {
						DateTime dateTime = parser2.parseDateTime(dateStr);
						date = dateTime.toDate();
					} catch (Exception e) {
						// ignore
					}
				}
			}
			if (date == null)
				continue;
			date.setSeconds(0);
			Long hits = log.hits.get(date);
			if (hits == null) {
				if (line.indexOf(" ERR ") != -1) {
					log.hits.put(date, 1L);
				} else {
					log.hits.put(date, 1L);
				}
			} else {
				if (line.indexOf(" ERR ") != -1) {
					log.hits.put(date, hits + 1L);
				} else {
					log.hits.put(date, hits + 1L);
				}
			}
		} while (true);

		IOUtils.closeQuietly(in);
		IOUtils.closeQuietly(is);
	}
}
