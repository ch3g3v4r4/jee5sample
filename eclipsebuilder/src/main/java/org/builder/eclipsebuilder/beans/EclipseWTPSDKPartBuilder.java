package org.builder.eclipsebuilder.beans;

import java.io.File;

import org.apache.log4j.Logger;

public class EclipseWTPSDKPartBuilder extends PartBuilderHelper implements PartBuilder {

    protected static Logger logger = Logger.getLogger(EclipseWTPSDKPartBuilder.class);

    private String downloadPage = "http://download.eclipse.org/webtools/downloads/";

    public void build(EclipseBuilderContext context) throws Exception {

        super.build(context);

        logger.info("Looking for the Eclipse WTP SDK hyperlink.");
        String[] downloadLinkAndChecksumLink = getDownloadAndChecksumLinks(downloadPage, "wtp-sdk", context.getBuildType());
        String downloadLink = downloadLinkAndChecksumLink[0];
        String checksumLink = downloadLinkAndChecksumLink[1];
        logger.info("Eclipse WTP SDK hyperlink: " + downloadLink + "; checksum link:" + checksumLink);

        File file = downloadAndCheck(downloadLink, checksumLink, context.getCacheHome());

        // unzip to target folder
        unzip(file, context.getEclipseHome(), true);
    }

    public void copyFile(File file, File targetDir) {
    }

}
