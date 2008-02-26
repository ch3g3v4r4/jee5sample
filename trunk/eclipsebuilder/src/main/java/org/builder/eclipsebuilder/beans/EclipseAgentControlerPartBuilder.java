package org.builder.eclipsebuilder.beans;

import java.io.File;

import org.apache.log4j.Logger;

public class EclipseAgentControlerPartBuilder extends PartBuilderHelper implements PartBuilder {

    protected static Logger logger = Logger.getLogger(EclipseAgentControlerPartBuilder.class);

    private String downloadPage = "http://www.eclipse.org/tptp/home/downloads/";

    public void build(EclipseBuilderContext context) throws Exception {

        super.build(context);

        logger.info("Looking for the Eclipse AgentController hyperlink.");
        String[] downloadLinkAndChecksumLink = getDownloadAndChecksumLinks(downloadPage, "agntctrl.win_ia32.sdk-TPTP", context.getBuildType());
        String downloadLink = downloadLinkAndChecksumLink[0];
        String checksumLink = downloadLinkAndChecksumLink[1];
        logger.info("Eclipse AgentController hyperlink: " + downloadLink + "; checksum link:" + checksumLink);

        File file = downloadAndCheck(downloadLink, checksumLink, context.getCacheHome());
        String fileName = file.getName();
        if (fileName.endsWith(".zip")) {
            fileName = fileName.substring(0, fileName.length() - ".zip".length());
        }
        File dir = new File(context.getEclipseHome(), fileName);
        dir.mkdir();

        // unzip to target folder
        unzip(file, dir, true);
    }

    public void copyFile(File file, File targetDir) {
    }
}
