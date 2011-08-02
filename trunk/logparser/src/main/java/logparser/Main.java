package logparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;

public class Main {

	public static class LogHistory {
		public Map<Long, Long> hits = new HashMap<Long, Long>();
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

	}

	private static void parse(File file, LogHistory log) throws Exception {
		FileSystemManager fsManager = VFS.getManager();
		FileObject vfsfile;
		if (file.getName().endsWith(".gz")) {
			vfsfile = fsManager.resolveFile("gz:" + file.toURI().toString()
					+ "!" + file.getName().substring(0, file.getName().lastIndexOf(".gz")));
		} else {
			vfsfile = fsManager.resolveFile(file.toURI().toString());
		}
		InputStream is = vfsfile.getContent().getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(is));

		do {
			String line = in.readLine();
			if (line == null) break;
			// [24/Jul/2011:18:18:37 -0400] "GET / HTTP/1.1"
			// 2011-08-01T00:00:20-04:00 ERR (3): ...[Error message: Too many connections]

		} while (true);

		IOUtils.closeQuietly(in);
		IOUtils.closeQuietly(is);
	}
}
