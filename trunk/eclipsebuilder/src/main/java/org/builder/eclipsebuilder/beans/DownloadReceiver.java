package org.builder.eclipsebuilder.beans;

import java.io.File;
import java.net.URL;
import java.util.List;

public class DownloadReceiver {
    private URL url;
    private File file;
    private Long size;

    private List<Range> emptyParts;
    private List<Object> errors;

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
    public void setSize(Long size) {
        this.size = size;
    }
    public List<Range> getEmptyParts() {
        return emptyParts;
    }
    public void setEmptyParts(List<Range> emptyParts) {
        this.emptyParts = emptyParts;
    }
    public List<Object> getErrors() {
        return errors;
    }
    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

    public boolean isCompleted() {
        // TODO Auto-generated method stub
        return true;
    }
    public void write(long currentOffset, byte[] buffer, int read) {
        // TODO Auto-generated method stub

    }
}
