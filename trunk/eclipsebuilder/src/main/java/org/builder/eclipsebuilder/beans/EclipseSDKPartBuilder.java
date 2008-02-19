package org.builder.eclipsebuilder.beans;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.builder.eclipsebuilder.beans.Configuration.BuildType;

public class EclipseSDKPartBuilder implements PartBuilder {
    private static Logger logger = Logger.getLogger(EclipseSDKPartBuilder.class);

    private String downloadPage = "http://download.eclipse.org/eclipse/downloads/";
    private WebBrowser webBrowser;
    private DownloadManager downloadManager;

    public void setWebBrowser(WebBrowser webBrowser) {
        this.webBrowser = webBrowser;
    }

    public void setDownloadManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    public void build(EclipseBuilderContext context) throws Exception {
        logger.info("Looking for the Eclipse SDK hyperlink.");
        String[] downloadLinkAndChecksumLink = getDownloadLinkAndChecksumLink(context.getBuildType());
        String downloadLink = downloadLinkAndChecksumLink[0];
        String checksumLink = downloadLinkAndChecksumLink[1];
        logger.info("Eclipse SDK hyperlink: " + downloadLink + "; checksum link:" + checksumLink);

        // If the file is already downloaded, verify it checksum to determine download again or skip
        Object[] nameAndSize = getNameAndSize(new URL(downloadLink));
        String fileName = (String) nameAndSize[0];
        Long downloadSize = (Long) nameAndSize[1];
        logger.info("File name: " + fileName + "; file size:" + downloadSize);
        File file = new File(context.getCacheHome(), fileName);

        boolean fileExist = file.exists();
        boolean checksumValid = false;

        if (fileExist) {
            logger.info("Verifying Eclipse SDK checksum...");
            checksumValid = verifyChecksum(file, checksumLink);
        }

        if (!fileExist || !checksumValid) {
            logger.info("File is not found in cache or checksum is incorrect, will download Eclipse SDK.");
            downloadFile(downloadLink, file, downloadSize);
            fileExist = file.exists();
            if (fileExist) {
                logger.info("Eclipse SDK is downloaded to location:" + file.getAbsolutePath());
                logger.info("Verifying Eclipse SDK checksum...");
                checksumValid = verifyChecksum(file, checksumLink);
            } else {
                logger.error("Failed to download Eclipse SDK.");
                throw new Exception("Failed to download Eclipse SDK!");
            }
        }

        if (!checksumValid) {
            logger.warn("Failed to verify Eclipse SDK integrity.");
        } else {
            logger.info("Eclipse SDK integrity is good.");
        }
    }

    private boolean verifyChecksum(File file, String url) throws Exception {
        boolean verify = false;

        MessageDigest md = null;
        if (url.toString().endsWith(".md5")) {
            md = MessageDigest.getInstance("MD5");
        }
        if (url.toString().endsWith(".sha1")) {
            md = MessageDigest.getInstance("SHA-1");
        }
        String fileContent = webBrowser.getUrlContentAsText(url);
        if (md != null && fileContent != null) {
            String expectedChecksum = fileContent.split(" ")[0];
            String checksum = digest(file, md);
            verify = expectedChecksum.equals(checksum);
        }

        return verify;
    }

    private String digest(File file, MessageDigest md) throws Exception {
        String result = null;
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            byte buf[] = new byte[ 8192 ];
            int bytes = 0;
            while ( ( bytes = bis.read( buf ) ) != -1 ) {
                md.update( buf, 0, bytes );
            }
            char[] dg = Hex.encodeHex(md.digest());
            result = new String(dg);
        } finally {
            if (bis != null) {
                bis.close();
            }
        }
        return result;
    }

    private Object[] getNameAndSize(URL url) throws Exception {
        return this.webBrowser.getFileNameAndSize(url);
    }

    private void downloadFile(String downloadLink, File file, Long fileSize) throws Exception {
        downloadManager.setUrl(new URL(downloadLink));
        downloadManager.setFile(file);
        downloadManager.setFileSize(fileSize);
        downloadManager.setMaxThreads(10);
        downloadManager.setMaxTries(100);

        Thread thread = new Thread(downloadManager);
        logger.info("Starting download manager.");
        thread.start();
        logger.info("Waiting download manager to stop.");
        thread.join();
        if (!downloadManager.getErrors().isEmpty()) {
            throw downloadManager.getErrors().get(0);
        }
        logger.info("Download manager stopped.");
    }

    private String[] getDownloadLinkAndChecksumLink(BuildType buildType) throws Exception {
        String[] downloadLinkAndChecksumLink = new String[2];

        // http://download.eclipse.org/eclipse/downloads/drops/S-3.4M5-200802071530/index.php
        String link;
        if (buildType == BuildType.RELEASE) {
            String pattern = "drops/R-([^/]+)/index.php";
            link = webBrowser.getLink(this.downloadPage, pattern);
        } else if (buildType == BuildType.INTEGRATION) {
            String pattern = "drops/I([^/]+)/index.php";
            link = webBrowser.getLink(this.downloadPage, pattern);
        } else if (buildType == BuildType.NIGHTLY) {
            String pattern = "drops/N([^/]+)/index.php";
            link = webBrowser.getLink(this.downloadPage, pattern);
        } else { // STABLE
            String patternStr1 = "drops/S-([^/-]+)-([^/-]+)/index.php";
            String link1 = webBrowser.getLink(this.downloadPage, patternStr1);
            String patternStr2 = "drops/R-([^/-]+)-([^/-]+)/index.php";
            String link2 = webBrowser.getLink(this.downloadPage, patternStr2);
            if (link1 == null && link2 != null) {
                link = link2;
            } else if (link1 != null && link2 == null) {
                link = link1;
            } else if (link1 == null && link2 == null) {
                link = null;
            } else {
                Pattern pattern1 = Pattern.compile(patternStr1);
                Pattern pattern2 = Pattern.compile(patternStr2);
                Matcher m1 = pattern1.matcher(link1);
                Matcher m2 = pattern2.matcher(link2);
                m1.find();
                m2.find();
                String version1 = m1.group(1);
                String version2 = m2.group(1);
                if (version1.compareTo(version2) > 0) {
                    link = link1;
                } else {
                    link = link2;
                }
            }
        }
        logger.info("Eclipse SDK hyperlink 1: " + link);

        // http://download.eclipse.org/eclipse/downloads/drops/S-3.4M5-200802071530/download.php?dropFile=eclipse-SDK-3.4M5-win32.zip
        String link2;
        String pattern2 = "drops/([^/]+)/download.php\\?dropFile=eclipse-SDK-([^/]+)-win32.zip";
        link2 = webBrowser.getLink(link, pattern2);
        logger.info("Eclipse SDK hyperlink 2: " + link2);

        // http://www.eclipse.org/downloads/download.php?file=/eclipse/downloads/drops/S-3.4M5-200802071530/eclipse-SDK-3.4M5-win32.zip
        String link3;
        String pattern3 = "eclipse-SDK-([^/]+)-win32.zip";
        link3 = webBrowser.getLink(link2, pattern3);
        logger.info("Eclipse SDK hyperlink 3: " + link3);

        String downloadLink;
        String link3ContentType = webBrowser.getContentType(link3);
        if (link3ContentType != null && link3ContentType.startsWith("text")) {
            // http://www.eclipse.org/downloads/download.php?file=/eclipse/downloads/drops/S-3.4M5-200802071530/eclipse-SDK-3.4M5-win32.zip&url=http://download.eclipse.org/eclipse/downloads/drops/S-3.4M5-200802071530/eclipse-SDK-3.4M5-win32.zip&mirror_id=1
            String link4;
            String pattern4 = "url=http://download.eclipse.org/";
            link4 = webBrowser.getLink(link3, pattern4);
            downloadLink = link4;
        } else {
            downloadLink = link3;
        }
        logger.info("Eclipse SDK hyperlink for downloading: " + downloadLink);
        downloadLinkAndChecksumLink[0] = downloadLink;

        // Checksum link
        // http://download.eclipse.org/eclipse/downloads/drops/S-3.4M5-200802071530/checksum/eclipse-SDK-3.4M5-win32.zip.md5
        String checksumLink2;
        String checksumPattern2 = "eclipse-SDK-([^/]+)-win32.zip.md5";
        checksumLink2 = webBrowser.getLink(link, checksumPattern2);
        if (checksumLink2 == null) {
            checksumPattern2 = "eclipse-SDK-([^/]+)-win32.zip.sha1";
            checksumLink2 = webBrowser.getLink(link, checksumPattern2);
        }
        logger.info("Eclipse SDK checksum hyperlink: " + checksumLink2);
        downloadLinkAndChecksumLink[1] = checksumLink2;

        return downloadLinkAndChecksumLink;
    }

}
