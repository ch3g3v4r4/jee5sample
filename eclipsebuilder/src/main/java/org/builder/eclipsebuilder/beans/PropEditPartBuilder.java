package org.builder.eclipsebuilder.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.builder.eclipsebuilder.beans.Configuration.BuildType;

public class PropEditPartBuilder extends PartBuilderHelper implements PartBuilder {

    protected static Logger logger = Logger.getLogger(PropEditPartBuilder.class);

    protected List<String> filter(List<String> urlList, String artifactId, BuildType buildType) throws Exception {
        List<String> resultLinks = new ArrayList<String>();
        for (String url : urlList) {
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            if (fileName.endsWith(".zip")
                    && (artifactId == null || artifactId != null && fileName.contains(artifactId))) {
                resultLinks.add(url);
            }
        }

        return resultLinks;
    }

    protected void sortByArtifactVersion(List<String> links) {
        Collections.sort(links, new Comparator<String>() {
            public int compare(String l1, String l2) {
                int c;
                Artifact a1 = null;
                Artifact a2 = null;
                try {
                    a1 = DownloadLinkUtils.parseDownloadLink(l1);
                } catch (Exception e) {
                    logger.error("Cannot parse!", e);
                }
                try {
                    a2 = DownloadLinkUtils.parseDownloadLink(l2);
                } catch (Exception e) {
                    logger.error("Cannot parse!", e);
                }
                c = a1.getFileName().compareTo(a2.getFileName());
                return c;
            }

        });
    }
}
