package org.builder.eclipsebuilder.beans;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.message.BasicHeader;

public class DownloadWorker extends Thread {

    private DownloadReceiver receiver;
    private long offset;
    private Long size;

    private WebBrowser webBrowser;

    public DownloadWorker(DownloadReceiver receiver, long offset, Long size, WebBrowser webBrowser) {
        this.receiver = receiver;
        this.offset = offset;
        this.size = size;
        this.webBrowser = webBrowser;
    }

    public void run() {

        try {
            URL url = receiver.getUrl();
            HttpClient client = webBrowser.getHttpClient();
            HttpGet httpget = new HttpGet(url.toURI());

            if (offset > 0) {
                List<BasicHeader> defaultHeaders = new ArrayList<BasicHeader>(1);
                defaultHeaders.add(new BasicHeader("Range", "bytes=" + offset + "-"));
                httpget.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, defaultHeaders);
            }

            try {
                HttpResponse response = client.execute(httpget);
                int statusCode = response.getStatusLine().getStatusCode();
                if (offset > 0 &&  statusCode != HttpStatus.SC_PARTIAL_CONTENT
                        || offset == 0 && statusCode != HttpStatus.SC_OK) {
                    throw new IllegalArgumentException("Invalid reponse!");
                }

                InputStream is = new BufferedInputStream(response.getEntity().getContent());
                byte[] buffer = new byte[10 * 1024];
                long remainingCount = size != null ? size.longValue() : Long.MAX_VALUE;
                int read = 0;
                long currentOffset = offset;
                while (remainingCount > 0 && -1 != (read = is.read(buffer))) {

                    if (read > remainingCount) {
                        read = (int) remainingCount;
                    }

                    synchronized (receiver) {
                        receiver.write(currentOffset, buffer, read);
                    }

                    remainingCount = remainingCount - read;
                    currentOffset = currentOffset + read;
                }

                if (remainingCount != 0 && size != null) {
                    throw new IllegalArgumentException("Insufficient response data!");
                }

            } finally {
                httpget.abort();
            }
        } catch (Exception e) {
            synchronized (receiver) {
                List<Exception> errors = new ArrayList<Exception>();
                errors.addAll(receiver.getErrors());
                errors.add(e);
                receiver.setErrors(errors);
            }
        }
    }
}
