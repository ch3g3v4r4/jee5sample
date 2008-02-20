package org.builder.eclipsebuilder.beans;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.builder.eclipsebuilder.beans.Configuration.BuildType;

public class PartBuilderHelper {

    private static Logger logger = Logger.getLogger(PartBuilderHelper.class);

    protected WebBrowser webBrowser;
    private DownloadManager downloadManager;

    public void setWebBrowser(WebBrowser webBrowser) {
        this.webBrowser = webBrowser;
    }

    public void setDownloadManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    protected boolean verifyChecksum(File file, String url) throws Exception {
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
            byte buf[] = new byte[8192];
            int bytes = 0;
            while ((bytes = bis.read(buf)) != -1) {
                md.update(buf, 0, bytes);
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

    protected Object[] getNameAndSize(URL url) throws Exception {
        return this.webBrowser.getFileNameAndSize(url);
    }

    protected void downloadFile(String downloadLink, File file, Long fileSize)
            throws Exception {
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

    protected void unzip(File zipFile, File targetFolder, boolean overwrite)
            throws Exception {
        logger.info("Begin unzipping file:" + zipFile.getName()
                + " to folder: " + targetFolder);
        ZipEntry entry;
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(zipFile));
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String destFN = targetFolder.getAbsolutePath()
                            + File.separator + entry.getName();
                    createDirectories(destFN);
                    File targetFile = new File(destFN);
                    if (!targetFile.exists() || overwrite) {
                        FileOutputStream fos = new FileOutputStream(destFN);
                        try {
                            IOUtils.copy(zis, fos);
                        } finally {
                            fos.close();
                        }
                    }
                }
            }
        } finally {
            if (zis != null)
                zis.close();
        }
        logger.info("Unzipping completed.");
    }

    private void createDirectories(String filePath) {
        File file = new File(filePath);

        // List all directories to be created
        File directory = file.getParentFile();
        List<File> directories = new ArrayList<File>();
        while (directory != null && !directory.exists()) {
            directories.add(directory);
            directory = directory.getParentFile();
        }

        // Create directories (in reverse order)
        Collections.reverse(directories);
        for (File dir : directories) {
            dir.mkdir();
        }
    };

    protected File download(String url, String artifactId,
            BuildType buildType, File cacheFolder, File targetFolder) throws Exception {

        String downloadArtifactUrl = null;
        LinkedHashMap<String, List<String>> urlsOnDownloadPath = new LinkedHashMap<String, List<String>>();

        String currentUrl = url;
        List<String> contentTypeList;
        List<String> urlList;
        do {
            urlList = new ArrayList<String>();
            contentTypeList = new ArrayList<String>();
            webBrowser.getLinksAndContenType(currentUrl, urlList, contentTypeList);
            String contentType = contentTypeList.get(0);
            if (contentType.startsWith("text/html")) {
                urlsOnDownloadPath.put(currentUrl, urlList);
                currentUrl = selectBestNextUrl(artifactId, buildType, urlList);
            } else {
                urlsOnDownloadPath.put(currentUrl, null);
                downloadArtifactUrl = currentUrl;
                break;
            }

        } while (true);

        System.out.println(downloadArtifactUrl);
        System.out.println(urlsOnDownloadPath);

        return null;
    }

    private String selectBestNextUrl(String artifactId, BuildType buildType,
            List<String> urlList) throws Exception {
        String result;

        List<String> links = filter(urlList, artifactId, buildType);
        if (!links.isEmpty()) {
            sortByArtifactVersion(links);
            result = links.get(links.size() - 1);
        } else {
            result = null;
        }

        return result;
    }

    private void sortByArtifactVersion(List<String> links) {
        Collections.sort(links, new Comparator<String>() {
            public int compare(String l1, String l2) {
                int c;
                Artifact a1 = null;
                Artifact a2 = null;
                try {
                    a1 = DownloadLinkUtils.parseDownloadLink(l1);
                } catch (Exception e) {
                    logger.error("Cannot parse!", e);
                }
                try {
                    a2 = DownloadLinkUtils.parseDownloadLink(l2);
                } catch (Exception e) {
                    logger.error("Cannot parse!", e);
                }
                if (a1 == a2) c = 0;
                else if (a1 == null && a2 != null) c = -1;
                else if (a1 != null && a2 == null) c = 1;
                else {
                    String v1 = a1.getVersion();
                    String v2 = a2.getVersion();
                    if (v1 == v2) c = 0;
                    else if (v1 == null && v2 != null) c = -1;
                    else if (v1 != null && v2 == null) c = 1;
                    else c = v1.compareTo(v2);
                }
                return c;
            }
        });
    }

    private List<String> filter(List<String> urlList, String artifactId, BuildType buildType)
        throws Exception {

        List<String> artifactLinks = new ArrayList<String>();
        for (String urlStr : urlList) {
            Artifact artifact = DownloadLinkUtils.parseDownloadLink(urlStr);
            if (artifact == null) continue;
            if (artifactId != null && !artifactId.equals(artifact.getArtifactId())) continue;
            if (buildType != null) {
                if (artifact.getBuildType() == null) continue;
                List<BuildType> values = Arrays.asList(BuildType.values());
                if (values.indexOf(artifact.getBuildType()) > values.indexOf(buildType)) continue;
            }
            artifactLinks.add(urlStr);
        }

        return artifactLinks;
    }
}
