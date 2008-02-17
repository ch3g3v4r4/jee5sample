package org.builder.eclipsebuilder.beans;

public class DownloadWorker extends Thread {

    private DownloadReceiver receiver;
    private long offset;
    private Long size;

    public DownloadWorker(DownloadReceiver receiver, long offset, Long size) {
        this.receiver = receiver;
        this.offset = offset;
        this.size = size;
    }

    public void run() {
    }
}
