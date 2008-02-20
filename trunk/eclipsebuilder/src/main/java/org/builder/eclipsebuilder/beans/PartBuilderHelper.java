package org.builder.eclipsebuilder.beans;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

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

}
