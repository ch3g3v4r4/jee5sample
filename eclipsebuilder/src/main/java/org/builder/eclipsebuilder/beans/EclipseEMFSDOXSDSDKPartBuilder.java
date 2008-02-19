package org.builder.eclipsebuilder.beans;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;
import org.builder.eclipsebuilder.beans.Configuration.BuildType;

public class EclipseEMFSDOXSDSDKPartBuilder extends PartBuilderHelper implements PartBuilder {

    protected static Logger logger = Logger.getLogger(EclipseEMFSDOXSDSDKPartBuilder.class);

    private String downloadPage = "http://www.eclipse.org/modeling/emf/downloads/";

    public void build(EclipseBuilderContext context) throws Exception {
        logger.info("Looking for the Eclipse EMF, SDO, and XSD SDK hyperlink.");
        String[] downloadLinkAndChecksumLink = getDownloadLinkAndChecksumLink(context.getBuildType());
        String downloadLink = downloadLinkAndChecksumLink[0];
        String checksumLink = downloadLinkAndChecksumLink[1];
        logger.info("Eclipse EMF, SDO, and XSD SDK hyperlink: " + downloadLink + "; checksum link:" + checksumLink);

        // If the file is already downloaded, verify it checksum to determine download again or skip
        Object[] nameAndSize = getNameAndSize(new URL(downloadLink));
        String fileName = (String) nameAndSize[0];
        Long downloadSize = (Long) nameAndSize[1];
        logger.info("File name: " + fileName + "; file size:" + downloadSize);
        File file = new File(context.getCacheHome(), fileName);

        boolean fileExist = file.exists();
        boolean checksumValid = false;

        if (fileExist) {
            logger.info("Verifying Eclipse EMF, SDO, and XSD SDK checksum...");
            checksumValid = verifyChecksum(file, checksumLink);
        }

        if (!fileExist || !checksumValid) {
            logger.info("File is not found in cache or checksum is incorrect, will download Eclipse EMF, SDO, and XSD SDK.");
            downloadFile(downloadLink, file, downloadSize);
            fileExist = file.exists();
            if (fileExist) {
                logger.info("Eclipse EMF, SDO, and XSD SDK is downloaded to location:" + file.getAbsolutePath());
                logger.info("Verifying Eclipse EMF, SDO, and XSD SDK checksum...");
                checksumValid = verifyChecksum(file, checksumLink);
            } else {
                logger.error("Failed to download Eclipse EMF, SDO, and XSD SDK.");
                throw new Exception("Failed to download Eclipse EMF, SDO, and XSD SDK!");
            }
        }

        if (!checksumValid) {
            logger.warn("Failed to verify Eclipse EMF, SDO, and XSD SDK integrity.");
        } else {
            logger.info("Eclipse EMF, SDO, and XSD SDK integrity is good.");
        }

        // unzip to target folder
        unzip(file, context.getEclipseHome(), true);
    }

    private String[] getDownloadLinkAndChecksumLink(BuildType buildType) throws Exception {
        String[] downloadLinkAndChecksumLink = new String[2];

        // http://www.eclipse.org/downloads/download.php?file=/modeling/emf/emf/downloads/drops/2.4.0/S200802090050/emf-sdo-xsd-SDK-2.4.0M5.zip
        String link;
        if (buildType == BuildType.RELEASE) {
            String pattern = "/R([^/]+)/emf-sdo-xsd-SDK-";
            link = webBrowser.getLink(this.downloadPage, pattern);
        } else if (buildType == BuildType.INTEGRATION) {
            String pattern = "/I([^/]+)/emf-sdo-xsd-SDK-";
            link = webBrowser.getLink(this.downloadPage, pattern);
        } else { // STABLE
            String patternStr1 = "/R([^/]+)/emf-sdo-xsd-SDK-";
            String link1 = webBrowser.getLink(this.downloadPage, patternStr1);
            String patternStr2 = "/S([^/]+)/emf-sdo-xsd-SDK-";
            String link2 = webBrowser.getLink(this.downloadPage, patternStr2);
            if (link1 == null && link2 != null) {
                link = link2;
            } else if (link1 != null && link2 == null) {
                link = link1;
            } else if (link1 == null && link2 == null) {
                link = null;
            } else {
                if (link1.compareTo(link2) > 0) {
                    link = link1;
                } else {
                    link = link2;
                }
            }
        }
        logger.info("Eclipse EMF, SDO, and XSD SDK hyperlink 1: " + link);

        // http://www.eclipse.org/downloads/download.php?file=/modeling/emf/emf/downloads/drops/2.4.0/S200802090050/emf-sdo-xsd-SDK-2.4.0M5.zip&url=http://download.eclipse.org/modeling/emf/emf/downloads/drops/2.4.0/S200802090050/emf-sdo-xsd-SDK-2.4.0M5.zip&mirror_id=1
        String link2;
        String pattern2 = "url=http://download.eclipse.org/";
        link2 = webBrowser.getLink(link, pattern2);
        logger.info("Eclipse EMF, SDO, and XSD SDK hyperlink 2: " + link2);

        String downloadLink = link2;
        logger.info("Eclipse EMF, SDO, and XSD SDK hyperlink for downloading: " + downloadLink);
        downloadLinkAndChecksumLink[0] = downloadLink;

        // Checksum link
        // http://download.eclipse.org/modeling/emf/emf/downloads/drops/2.4.0/S200802090050/emf-sdo-xsd-SDK-2.4.0M5.zip.md5
        // http://www.eclipse.org/downloads/download.php?file=/modeling/emf/emf/downloads/drops/2.4.0/S200802090050/emf-sdo-xsd-SDK-2.4.0M5.zip
        String checksumLink;
        if (buildType == BuildType.RELEASE) {
            String pattern = "/R[^/]+/emf-sdo-xsd-SDK-[^/]+\\.zip\\.md5";
            checksumLink = webBrowser.getLink(this.downloadPage, pattern);
        } else if (buildType == BuildType.INTEGRATION) {
            String pattern = "/I[^/]+/emf-sdo-xsd-SDK-[^/]+\\.zip\\.md5";
            checksumLink = webBrowser.getLink(this.downloadPage, pattern);
        } else { // STABLE
            String patternStr1 = "/R[^/]+/emf-sdo-xsd-SDK-[^/]+\\.zip\\.md5";
            String md5link1 = webBrowser.getLink(this.downloadPage, patternStr1);
            String patternStr2 = "/S[^/]+/emf-sdo-xsd-SDK-[^/]+\\.zip\\.md5";
            String md5link2 = webBrowser.getLink(this.downloadPage, patternStr2);
            if (md5link1 == null && md5link2 != null) {
                checksumLink = md5link2;
            } else if (md5link1 != null && md5link2 == null) {
                checksumLink = md5link1;
            } else if (md5link1 == null && md5link2 == null) {
                checksumLink = null;
            } else {
                if (md5link1.compareTo(md5link2) > 0) {
                    checksumLink = md5link1;
                } else {
                    checksumLink = md5link2;
                }
            }
        }
        logger.info("Eclipse EMF, SDO, and XSD SDK checksum hyperlink: " + checksumLink);
        downloadLinkAndChecksumLink[1] = checksumLink;

        return downloadLinkAndChecksumLink;
    }

}
