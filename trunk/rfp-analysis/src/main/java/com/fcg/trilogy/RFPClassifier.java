package com.fcg.trilogy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RFPClassifier {
	private static String[] pattern2Project = {
			"^A7", "A7",
			"^A9000", "A9000",
			"^dos", "DOS",
			"^DOS", "DOS",
			"^cbc", "CBC",
			"^Zozoc", "ZOZOC",
			"^Views", "VIEWS",
			"^Family", "FAMILY",
			"^Pricer", "PRICER",
			"^AutoPage", "AUTOPAGE",
			"^CarsComplete", "CARSCOMPLETE",
			"^IRS", "IRS",
			"^([A-Z]{2,})", null };

	public static void test() throws IOException {
		Set names = new HashSet();
		BufferedReader in = new BufferedReader(new FileReader("a.txt"));
		String str;
		while ((str = in.readLine()) != null) {
			if (!str.trim().equals(""))
				names.add(str);
			System.out.println(str);
		}
		in.close();

		Map class2Names = classify(names);
		for (Iterator it = class2Names.keySet().iterator(); it.hasNext();) {
			String group = (String) it.next();
			System.out.println(">>>>>>>>>>>>>" + group);
			System.out.println(class2Names.get(group));
		}
		System.out.println(class2Names.keySet());
	}

	public static Map<String, Set<String>> classify(Set<String> names) {
		Map<String, Set<String>> class2Name = new Hashtable<String, Set<String>>();
		Set<String> unknownStrings = new TreeSet<String>();
		for (Iterator<String> it = names.iterator(); it.hasNext();) {
			String name = it.next();

			boolean foundGroup = false;
			for (int i = 0; i < pattern2Project.length; i += 2) {
				String pattern = pattern2Project[i];
				String projectName = pattern2Project[i + 1];
		        Pattern p = Pattern.compile(pattern);
		        Matcher matcher = p.matcher(name);
				if (matcher.find()) {
					if (projectName == null) projectName = matcher.group(1);
					putItem2Group(class2Name, name, projectName);
					foundGroup = true;
					break;
				}
			}
			if (!foundGroup) {
				unknownStrings.add(name);
			}
		}
		for (Iterator<String> it = unknownStrings.iterator(); it.hasNext();) {
			String unknown = it.next();
			boolean foundGroup = false;
			for (Iterator<String> it2 = class2Name.keySet().iterator(); it2.hasNext();) {
				String group = it2.next();
				if (unknown.toUpperCase().indexOf(group.toUpperCase()) >= 0) {
					putItem2Group(class2Name, unknown, group);
					foundGroup = true;
					break;
				}
			}
			if (!foundGroup) {
				putItem2Group(class2Name, unknown, "Unknown");
			}
		}
		return class2Name;
	}

	private static void putItem2Group(Map<String, Set<String>> class2Name,
			String name, String projectName) {
		Set<String> strings = class2Name.get(projectName);
		if (strings == null) {
			strings = new TreeSet<String>();
			class2Name.put(projectName, strings);
		}
		strings.add(name);
	}
}
