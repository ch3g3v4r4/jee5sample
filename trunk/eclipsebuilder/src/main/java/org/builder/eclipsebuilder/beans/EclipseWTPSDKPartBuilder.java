package org.builder.eclipsebuilder.beans;

import java.io.File;
import java.util.List;

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
//
//    private String[] getDownloadLinkAndChecksumLink(BuildType buildType) throws Exception {
//        String[] downloadLinkAndChecksumLink = new String[2];
//
//        // http://download.eclipse.org/webtools/downloads/drops/R3.0/S-3.0M5-20080218021547/
//        String link;
//        if (buildType == BuildType.RELEASE) {
//            String pattern = "/R-([^/]+)/";
//            link = webBrowser.getLink(this.downloadPage, pattern);
//        } else if (buildType == BuildType.INTEGRATION) {
//            String pattern = "/I-([^/]+)/";
//            link = webBrowser.getLink(this.downloadPage, pattern);
//        } else if (buildType == BuildType.NIGHTLY) {
//            String pattern = "/N([^/]+)/";
//            link = webBrowser.getLink(this.downloadPage, pattern);
//        } else { // STABLE
//            String patternStr1 = "/S-([^/]+)/";
//            String link1 = webBrowser.getLink(this.downloadPage, patternStr1);
//            String patternStr2 = "/R-([^/]+)/";
//            String link2 = webBrowser.getLink(this.downloadPage, patternStr2);
//            if (link1 == null && link2 != null) {
//                link = link2;
//            } else if (link1 != null && link2 == null) {
//                link = link1;
//            } else if (link1 == null && link2 == null) {
//                link = null;
//            } else {
//                Pattern pattern1 = Pattern.compile(patternStr1);
//                Pattern pattern2 = Pattern.compile(patternStr2);
//                Matcher m1 = pattern1.matcher(link1);
//                Matcher m2 = pattern2.matcher(link2);
//                m1.find();
//                m2.find();
//                String version1 = m1.group(1);
//                String version2 = m2.group(1);
//                if (version1.compareTo(version2) > 0) {
//                    link = link1;
//                } else {
//                    link = link2;
//                }
//            }
//        }
//        logger.info("Eclipse WTP SDK hyperlink 1: " + link);
//
//        // http://www.eclipse.org/downloads/download.php?file=/webtools/downloads/drops/R3.0/S-3.0M5-20080218021547/wtp-sdk-S-3.0M5-20080218021547.zip
//        String link2;
//        String pattern2 = "/wtp-sdk-";
//        link2 = webBrowser.getLink(link, pattern2);
//        logger.info("Eclipse WTP SDK hyperlink 2: " + link2);
//
//        // http://www.eclipse.org/downloads/download.php?file=/webtools/downloads/drops/R3.0/S-3.0M5-20080218021547/wtp-sdk-S-3.0M5-20080218021547.zip&url=http://download.eclipse.org/webtools/downloads/drops/R3.0/S-3.0M5-20080218021547/wtp-sdk-S-3.0M5-20080218021547.zip&mirror_id=1
//        String link3;
//        String pattern3 = "url=http://download.eclipse.org/";
//        link3 = webBrowser.getLink(link2, pattern3);
//        logger.info("Eclipse WTP SDK hyperlink 3: " + link3);
//
//        String downloadLink = link3;
//        logger.info("Eclipse WTP SDK hyperlink for downloading: " + downloadLink);
//        downloadLinkAndChecksumLink[0] = downloadLink;
//
//        // Checksum link
//        // http://download.eclipse.org/webtools/downloads/drops/R3.0/S-3.0M5-20080218021547/checksum/wtp-sdk-S-3.0M5-20080218021547.zip.md5
//        String checksumLink2;
//        String checksumPattern2 = "/wtp-sdk-.+\\.zip\\.md5";
//        checksumLink2 = webBrowser.getLink(link, checksumPattern2);
//        if (checksumLink2 == null) {
//            checksumPattern2 = "/wtp-sdk-.+\\.zip\\.sha1";
//            checksumLink2 = webBrowser.getLink(link, checksumPattern2);
//        }
//        logger.info("Eclipse SDK checksum hyperlink: " + checksumLink2);
//        downloadLinkAndChecksumLink[1] = checksumLink2;
//
//        return downloadLinkAndChecksumLink;
//    }

}
