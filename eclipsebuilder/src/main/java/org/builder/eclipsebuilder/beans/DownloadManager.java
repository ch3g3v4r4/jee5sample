package org.builder.eclipsebuilder.beans;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class DownloadManager implements Runnable {

    private URL url;
    private File file;
    private Long fileSize;

    private int maxThreads = 10;
    private int maxTries = 10;
    private DownloadReceiver receiver;

    private Exception error;

    private WebBrowser webBrowser;

    public void setWebBrowser(WebBrowser webBrowser) {
        this.webBrowser = webBrowser;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public void setMaxTries(int maxTries) {
        this.maxTries = maxTries;
    }

    public File getFile() {
        return this.file;
    }

    public void run() {
        try {
            download();
        } catch (Exception e) {
            error = e;
        }
    }

    private void download() throws Exception {
        receiver = new DownloadReceiver();
        receiver.setUrl(url);
        receiver.setFile(file);
        receiver.setSize(fileSize);
        List<Range> parts = new ArrayList<Range>();
        Range range = new Range();
        range.setOffset(0);
        range.setSize(fileSize);
        parts.add(range);
        receiver.setEmptyParts(parts);

        int tryNum = 0;
        do {

            receiver.setErrors(new ArrayList<Exception>());
            List<DownloadWorker> workers = initializeWorkers(receiver, maxThreads);
            for (DownloadWorker worker : workers) {
                worker.start();
            }
            for (DownloadWorker worker : workers) {
                worker.join();
            }

            // Adjust number of threads using previous result
            int successNum = workers.size() - receiver.getErrors().size();
            if (successNum > 0) {
                maxThreads = successNum;
            } else {
                maxThreads = 1;
            }
            tryNum += receiver.getErrors().size();

        } while (!receiver.isCompleted() && tryNum < maxTries);

        if (!receiver.isCompleted() && tryNum >= maxTries) {
            error = new Exception("Could not complete downloading, too many tries!");
        }
    }

    private List<DownloadWorker> initializeWorkers(DownloadReceiver receiver, int maxThreads) {
        List<DownloadWorker> workers = new ArrayList<DownloadWorker>();

        List<Range> ranges  = new ArrayList<Range>();
        List<Range> emptyParts = receiver.getEmptyParts();
        if (maxThreads <= emptyParts.size()) {
            ranges.addAll(emptyParts);
        } else {
            ranges.addAll(emptyParts);
            List<Range> remainingRanges  = new ArrayList<Range>();
            for (Iterator<Range> it = ranges.iterator(); it.hasNext(); ) {
                Range range = it.next();
                if (range.getSize() != null) {
                    it.remove();
                    remainingRanges.add(range);
                }
            }
            if (!remainingRanges.isEmpty()) {
                int remainingThreads = maxThreads - ranges.size();
                while (canSplit(remainingRanges) && remainingRanges.size() < remainingThreads) {
                    split(remainingRanges);
                }
                ranges.addAll(remainingRanges);
            }
        }

        // Sort by offset
        Collections.sort(ranges, new Comparator<Range>() {
            public int compare(Range o1, Range o2) {
                return (int) (o1.getOffset() - o2.getOffset());
            }
        });
        int partIndex = 0;
        for (Range range : ranges) {
            if (partIndex < maxThreads) {
                DownloadWorker worker = new DownloadWorker(receiver, range.getOffset(), range.getSize(), webBrowser);
                workers.add(worker);
            }
            partIndex ++;
        }
        receiver.setEmptyParts(ranges);
        return workers;
    }

    private static void split(List<Range> ranges) {
        Range biggest = getBiggest(ranges);
        ranges.remove(biggest);
        //split biggest into 2 ranges
        long size1 = biggest.getSize().longValue() / 2;
        long size2 = biggest.getSize().longValue() - size1;
        Range range1 = new Range();
        range1.setOffset(biggest.getOffset());
        range1.setSize(size1);
        Range range2 = new Range();
        range2.setOffset(biggest.getOffset() + size1);
        range2.setSize(size2);
        ranges.add(range1);
        ranges.add(range2);
    }

    private static boolean canSplit(List<Range> remainingRanges) {
        Range biggest = getBiggest(remainingRanges);
        return (biggest != null) && (biggest.getSize() > 4000);
    }

    private static Range getBiggest(List<Range> ranges) {
        Range biggest = null;
        for (Range range : ranges) {
            if (range.getSize() != null) {
                if (biggest == null || biggest.getSize() < range.getSize()) {
                    biggest = range;
                }
            }
        }
        return biggest;
    }


    public List<Exception> getErrors() {
        List<Exception> errors = new ArrayList<Exception>();
        if (error != null) {
            errors.add(error);
        }
        if (receiver != null && receiver.getErrors() != null) {
            errors.addAll(receiver.getErrors());
        }
        return errors;
    }
}
