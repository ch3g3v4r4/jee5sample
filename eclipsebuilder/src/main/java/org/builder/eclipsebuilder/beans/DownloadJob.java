package org.builder.eclipsebuilder.beans;

import java.io.File;
import java.net.URL;
import java.util.List;

public class DownloadJob {

    public static enum Status {STARTING, STOPPING, STOPPED, SUCCESS} ;

    private URL url;
    private File file;
    private Long size;

    private List<Range> downloadingRanges;
    private long completedBytes;
    private Status status;

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
    public long getCompletedBytes() {
        return completedBytes;
    }
    public void setCompletedBytes(long completedBytes) {
        this.completedBytes = completedBytes;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
}
