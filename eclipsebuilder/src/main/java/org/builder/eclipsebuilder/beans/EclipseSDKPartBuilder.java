package org.builder.eclipsebuilder.beans;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.builder.eclipsebuilder.beans.Configuration.BuildType;

public class EclipseSDKPartBuilder implements PartBuilder {

    private String downloadPage = "http://download.eclipse.org/eclipse/downloads/";
    private WebBrowser webBrowser;

    public void setWebBrowser(WebBrowser webBrowser) {
        this.webBrowser = webBrowser;
    }

    public void build(EclipseBuilderContext context) throws Exception {

        String downloadLink = getDownloadLink(context.getBuildType());
        File downloadedFile = downloadFile(downloadLink, context.getCacheHome());

        System.out.println(downloadLink);

    }

    private File downloadFile(String downloadLink, File cacheHome) {
        // TODO Auto-generated method stub
        return null;
    }

    private String getDownloadLink(BuildType buildType) throws Exception {

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

        // http://download.eclipse.org/eclipse/downloads/drops/S-3.4M5-200802071530/download.php?dropFile=eclipse-SDK-3.4M5-win32.zip
        String link2;
        String pattern2 = "drops/([^/]+)/download.php\\?dropFile=eclipse-SDK-([^/]+)-win32.zip";
        link2 = webBrowser.getLink(link, pattern2);

        // http://www.eclipse.org/downloads/download.php?file=/eclipse/downloads/drops/S-3.4M5-200802071530/eclipse-SDK-3.4M5-win32.zip
        String link3;
        String pattern3 = "eclipse-SDK-([^/]+)-win32.zip";
        link3 = webBrowser.getLink(link2, pattern3);

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

        return downloadLink;
    }

}
