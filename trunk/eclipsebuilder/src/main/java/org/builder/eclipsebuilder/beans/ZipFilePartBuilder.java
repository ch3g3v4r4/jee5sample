package org.builder.eclipsebuilder.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.builder.eclipsebuilder.beans.Configuration.BuildType;

public class ZipFilePartBuilder extends PartBuilderHelper implements PartBuilder {

    protected static Logger logger = Logger.getLogger(ZipFilePartBuilder.class);

    protected List<String> filter(List<String> urlList, String artifactId, BuildType buildType) throws Exception {
        List<String> resultLinks = new ArrayList<String>();
        for (String url : urlList) {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            if ((fileName.endsWith(".zip") || fileName.endsWith("&mirror_id=1")) && (artifactId == null || artifactId != null && fileName.contains(artifactId))) {
                resultLinks.add(url);
            }
        }

        return resultLinks;
    }
    
    protected void sortByArtifactVersion(List<String> links) {
        Collections.sort(links, new Comparator<String>() {
            public int compare(String l1, String l2) {
                int c;
                String n1 = l1.substring(l1.lastIndexOf('/') + 1);
                String n2 = l2.substring(l2.lastIndexOf('/') + 1);
                Pattern p = Pattern.compile("\\d+(\\.\\d+)*");
                Matcher m1 = p.matcher(n1);
                Matcher m2 = p.matcher(n2);
                if (m1.find() && m2.find()) {
                    String v1 = m1.group();
                    String v2 = m2.group();
                    if (v1.indexOf('.') == -1 && v2.indexOf('.') == -1 && v1.length() != v2.length()) {
                        int maxLength = Math.max(v1.length(), v2.length());
                        while (v1.length() < maxLength) v1 += '0';
                        while (v2.length() < maxLength) v2 += '0';
                    }
                    c = compareVersion(v1, v2);
                } else {
                    c = n1.compareTo(n2);
                }
                return c;
            }
        });
    }

}
