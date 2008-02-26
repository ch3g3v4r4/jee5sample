package org.builder.eclipsebuilder.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.builder.eclipsebuilder.beans.Configuration.BuildType;

public class SubclipsePartBuilder extends PartBuilderHelper implements PartBuilder {

    protected static Logger logger = Logger.getLogger(SubclipsePartBuilder.class);

    private String downloadPage = "http://subclipse.tigris.org/servlets/ProjectDocumentList?folderID=2240&expandFolder=2240&folderID=0";
    private String artifactId = "site";

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

    public void build(EclipseBuilderContext context) throws Exception {
        logger.info("Looking for the Subclipse hyperlink.");
        String[] downloadLinkAndChecksumLink = getDownloadAndChecksumLinks(downloadPage, artifactId, context.getBuildType());
        String downloadLink = downloadLinkAndChecksumLink[0];
        String checksumLink = downloadLinkAndChecksumLink[1];
        logger.info("Subclipse hyperlink: " + downloadLink + "; checksum link:" + checksumLink);

        File srcFile = downloadAndCheck(downloadLink, checksumLink, context.getCacheHome());

        installPart(srcFile, context.getEclipseHome(), true);
    }


}
