package org.builder.eclipsebuilder.beans;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.builder.eclipsebuilder.beans.Configuration.BuildType;

public class AndreiPartBuilder extends PartBuilderHelper implements PartBuilder {

    protected static Logger logger = Logger.getLogger(AndreiPartBuilder.class);

    protected List<String> filter(List<String> urlList, String artifactId, BuildType buildType) throws Exception {
        List<String> resultLinks = new ArrayList<String>();
        for (String url : urlList) {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            if (fileName.endsWith(".jar")
                    && (artifactId == null || artifactId != null && fileName.contains(artifactId))) {
                resultLinks.add(url);
            }
        }

        return resultLinks;
    }
}
