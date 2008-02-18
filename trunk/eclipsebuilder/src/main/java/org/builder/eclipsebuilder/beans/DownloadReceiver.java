package org.builder.eclipsebuilder.beans;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.List;

public class DownloadReceiver {
    private URL url;
    private File file;
    private Long size;

    private List<Range> emptyParts;
    private List<Exception> errors;

    public URL getUrl() {
        return url;
    }
    public void setUrl(URL url) {
        this.url = url;
    }
    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }
    public Long getSize() {
        return size;
    }
    public void setSize(Long size) throws Exception {
        // fsutil file createnew name size
        String[] command = new String[] {"fsutil", "file", "createnew", file.getAbsolutePath(), size == null ? "0" : size.toString()};
        Process child = Runtime.getRuntime().exec(command);
        child.waitFor();
        this.size = size;
    }
    public List<Range> getEmptyParts() {
        return emptyParts;
    }
    public void setEmptyParts(List<Range> emptyParts) {
        this.emptyParts = emptyParts;
    }
    public List<Exception> getErrors() {
        return errors;
    }
    public void setErrors(List<Exception> errors) {
        this.errors = errors;
    }

    public boolean isCompleted() {
        return (size == null) || (size.longValue() == writeSize);
    }

    private long writeSize = 0;
    public void write(long offset, byte[] buffer, int len) throws Exception {
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(offset);
        raf.write(buffer, 0, len);
        raf.close();
        writeSize += len;
    }
}
