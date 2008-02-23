package org.builder.eclipsebuilder.beans;

import java.io.File;

import org.apache.log4j.Logger;

public class EclipseTPTPSDKPartBuilder extends PartBuilderHelper implements PartBuilder {

    protected static Logger logger = Logger.getLogger(EclipseTPTPSDKPartBuilder.class);

    private String downloadPage = "http://www.eclipse.org/tptp/home/downloads/";

    public void build(EclipseBuilderContext context) throws Exception {
        super.build(context);

        logger.info("Looking for the Eclipse TPTP SDK hyperlink.");
        String[] downloadLinkAndChecksumLink = getDownloadAndChecksumLinks(downloadPage, "tptp.sdk-TPTP", context.getBuildType());
        String downloadLink = downloadLinkAndChecksumLink[0];
        String checksumLink = downloadLinkAndChecksumLink[1];
        logger.info("Eclipse TPTP SDK hyperlink: " + downloadLink + "; checksum link:" + checksumLink);

        File file = downloadAndCheck(downloadLink, checksumLink, context.getCacheHome());

        // unzip to target folder
        unzip(file, context.getEclipseHome(), true);
    }
}
