package org.builder.eclipsebuilder.beans;

import java.io.File;

import org.apache.log4j.Logger;

public class EclipseGEFSDKPartBuilder extends PartBuilderHelper implements PartBuilder {

    protected static Logger logger = Logger.getLogger(EclipseGEFSDKPartBuilder.class);

    private String downloadPage = "http://www.eclipse.org/gef/downloads/";

    public void build(EclipseBuilderContext context) throws Exception {
        logger.info("Looking for the Eclipse GEF SDK hyperlink.");
        String[] downloadLinkAndChecksumLink = getDownloadAndChecksumLinks(downloadPage, "GEF-ALL", context.getBuildType());
        String downloadLink = downloadLinkAndChecksumLink[0];
        String checksumLink = downloadLinkAndChecksumLink[1];
        logger.info("Eclipse GEF hyperlink: " + downloadLink + "; checksum link:" + checksumLink);

        File file = downloadAndCheck(downloadLink, checksumLink, context.getCacheHome());

        // unzip to target folder
        unzip(file, context.getEclipseHome(), true);
    }

    public void copyFile(File file, File targetDir) {
    }
}
