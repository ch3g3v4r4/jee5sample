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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private boolean verifyChecksum(File file, String url) throws Exception {
        boolean verify = false;
        if (url != null) {
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
        } else if (file.getName().endsWith(".zip")) {
            // try to test zip integrity
            File temp = File.createTempFile("tmp", ".tmp");
            temp.delete();
            temp.mkdir();
            try {
                unzip(file, temp, true);
                verify = true;
            } catch (Exception e) {
            }
            deleteDir(temp);
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

    private Object[] getNameAndSize(URL url) throws Exception {
        return this.webBrowser.getFileNameAndSize(url);
    }

    private void downloadFile(String downloadLink, File file, Long fileSize)
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

    protected File downloadAndCheck(String downloadLink, String checksumLink, File cacheFolder) throws Exception {
        // If the file is already downloaded, verify it checksum to determine download again or skip
        Object[] nameAndSize = getNameAndSize(new URL(downloadLink));
        String fileName = (String) nameAndSize[0];
        Long downloadSize = (Long) nameAndSize[1];
        logger.info("File name: " + fileName + "; file size:" + downloadSize);
        File file = new File(cacheFolder, fileName);

        boolean fileExist = file.exists();
        boolean checksumValid = false;

        if (fileExist) {
            logger.info("Verifying checksum...");
            checksumValid = verifyChecksum(file, checksumLink);
        }

        if (!fileExist || !checksumValid) {
            logger.info("File is not found in cache or checksum is incorrect, will download file.");
            downloadFile(downloadLink, file, downloadSize);
            fileExist = file.exists();
            if (fileExist) {
                logger.info("File is downloaded to location:" + file.getAbsolutePath());
                logger.info("Verifying checksum...");
                checksumValid = verifyChecksum(file, checksumLink);
            } else {
                logger.error("Failed to download file.");
                throw new Exception("Failed to download file!");
            }
        }

        if (!checksumValid) {
            logger.warn("Failed to verify file integrity.");
        } else {
            logger.info("File integrity is good.");
        }
        return file;
    }

    protected String[] getDownloadAndChecksumLinks(String url, String artifactId,
            BuildType buildType) throws Exception {

        String downloadArtifactUrl = null;
        LinkedHashMap<String, List<String>> urlsOnDownloadPath = new LinkedHashMap<String, List<String>>();

        String currentUrl = url;
        List<String> contentTypeList;
        List<String> urlList;
        do {
            logger.info("Openning hyperlink: " + currentUrl);
            urlList = new ArrayList<String>();
            contentTypeList = new ArrayList<String>();
            webBrowser.getLinksAndContenType(currentUrl, urlList, contentTypeList);
            String contentType = contentTypeList.get(0);
            if (contentType.startsWith("text/html")) {
                urlsOnDownloadPath.put(currentUrl, urlList);
                currentUrl = selectBestNextUrl(artifactId, buildType, urlList);
                logger.info("Selected next hyperlink: " + currentUrl);
            } else {
                urlsOnDownloadPath.put(currentUrl, null);
                downloadArtifactUrl = currentUrl;
                break;
            }

        } while (true);


        String downloadArtifactChecksumUrl = null;
        if (downloadArtifactUrl != null) {
            String downloadFileName = DownloadLinkUtils.parseDownloadLink(downloadArtifactUrl).getFileName();
            String checksumFileName1 = downloadFileName + ".md5";
            String checksumFileName2 = downloadFileName + ".sha1";

            List<String> reversePath = new ArrayList<String>();
            for (Iterator<String> it = urlsOnDownloadPath.keySet().iterator(); it.hasNext();) {
                reversePath.add(it.next());
            }
            Collections.reverse(reversePath);
            outer:
            for (String pathElem : reversePath) {
                List<String> links = urlsOnDownloadPath.get(pathElem);
                if (links != null) {
                    for (String link : links) {
                        if (link.contains(checksumFileName1) || link.contains(checksumFileName2)) {
                            downloadArtifactChecksumUrl = link;
                            break outer;
                        }
                    }
                }
            }
        }
        return new String[] {downloadArtifactUrl, downloadArtifactChecksumUrl};
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
                    else {
                        // compare to non-empty version numbers
                        // standardize versions like 3.4.0M5 and 3.4M5 before comparing
                        Pattern p = Pattern.compile("^\\d+(\\.\\d+)+");
                        Matcher m1 = p.matcher(v1);
                        Matcher m2 = p.matcher(v2);
                        String v1g1 = "0";
                        String v1g2 = "Z";
                        if (m1.find()) {
                            v1g1 = m1.group(0);
                            v1g2 = v1.substring(v1g1.length());
                        }
                        String v2g1 = "0";
                        String v2g2 = "Z";
                        if (m2.find()) {
                            v2g1 = m2.group(0);
                            v2g2 = v2.substring(v2g1.length());
                        }
                        List<String> v1l = new LinkedList<String>(Arrays.asList(v1g1.split("\\.")));
                        List<String> v2l = new LinkedList<String>(Arrays.asList(v2g1.split("\\.")));
                        int maxSize = Math.max(v1l.size(), v2l.size());
                        List<Integer> v1in = new ArrayList<Integer>(v1l.size());
                        List<Integer> v2in = new ArrayList<Integer>(v2l.size());
                        for (String s : v1l) {
                            v1in.add(Integer.parseInt(s));
                        }
                        for (String s : v2l) {
                            v2in.add(Integer.parseInt(s));
                        }
                        while (v1in.size() < maxSize) v1in.add(0);
                        while (v2in.size() < maxSize) v2in.add(0);
                        c = 0;
                        int i = 0;
                        while (i < maxSize && c == 0) {
                            c = v1in.get(i) - v2in.get(i);
                            i++;
                        }
                        if (c == 0) {
                            c = v1g2.compareTo(v2g2);
                        }
                    }
                }
                return c;
            }
        });
    }

    private List<String> filter(List<String> urlList, String artifactId, BuildType buildType)
        throws Exception {

        List<String> resultLinks = new ArrayList<String>();

        List<String> artifactWin32Links = new ArrayList<String>();
        List<String> artifactNoIdLinks = new ArrayList<String>();
        List<String> artifactMirror1Links = new ArrayList<String>();
        for (String urlStr : urlList) {
            if (urlStr.indexOf("protocol") != -1 || urlStr.indexOf("format") != -1 ) continue;
            Artifact artifact = DownloadLinkUtils.parseDownloadLink(urlStr);
            if (artifact == null) continue;

            // build type
            if (buildType != null) {
                //if (artifact.getBuildType() == null) continue;
                List<BuildType> values = Arrays.asList(BuildType.values());
                if (values.indexOf(artifact.getBuildType()) > values.indexOf(buildType)) continue;
            }

            //artifact ID (sometimes links in the first page doesn't contain artifact ID but we must select)
            if (artifactId != null && !artifactId.equals(artifact.getArtifactId())) {
                if (artifact.getArtifactId() == null) artifactNoIdLinks.add(urlStr);
                continue;
            }

            //windows platform only
            if (!artifact.getFileName().endsWith(".zip")) continue;
            resultLinks.add(urlStr);
            if (artifact.getFileName().endsWith("-win32.zip")) artifactWin32Links.add(urlStr);

            if (urlStr.endsWith("&mirror_id=1")) artifactMirror1Links.add(urlStr);
        }

        if (resultLinks.isEmpty() && !artifactNoIdLinks.isEmpty()) resultLinks = artifactNoIdLinks;
        if (!artifactWin32Links.isEmpty()) resultLinks = artifactWin32Links;
        if (!artifactMirror1Links.isEmpty()) resultLinks = artifactMirror1Links;
        return resultLinks;
    }
}
