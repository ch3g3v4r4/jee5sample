package org.builder.eclipsebuilder.beans;

import java.io.File;

import org.apache.log4j.Logger;

public class EclipseBIRTSDKPartBuilder extends PartBuilderHelper implements PartBuilder {

    protected static Logger logger = Logger.getLogger(EclipseBIRTSDKPartBuilder.class);

    private String downloadPage = "http://download.eclipse.org/birt/downloads/build_list.php";

    public void build(EclipseBuilderContext context) throws Exception {
        logger.info("Looking for the Eclipse BIRT SDK hyperlink.");
        String[] downloadLinkAndChecksumLink = getDownloadAndChecksumLinks(downloadPage, "birt-report-framework-sdk", context.getBuildType());
        String downloadLink = downloadLinkAndChecksumLink[0];
        String checksumLink = downloadLinkAndChecksumLink[1];
        logger.info("Eclipse BIRT SDK hyperlink: " + downloadLink + "; checksum link:" + checksumLink);

        File file = downloadAndCheck(downloadLink, checksumLink, context.getCacheHome());

        // unzip to target folder
        unzip(file, context.getEclipseHome(), true);
    }

    public void copyFile(File file, File targetDir) {
    }
}
