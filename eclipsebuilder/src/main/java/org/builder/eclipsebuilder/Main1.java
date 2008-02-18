package org.builder.eclipsebuilder;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.PlainSocketFactory;
import org.apache.http.conn.Scheme;
import org.apache.http.conn.SchemeRegistry;
import org.apache.http.conn.params.HttpConnectionManagerParams;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class Main1 {

    public static void main(String[] args) throws Exception {

        HttpParams params = new BasicHttpParams();
        HttpConnectionManagerParams.setMaxTotalConnections(params, 100);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

        // Create and initialize scheme registry
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory
                .getSocketFactory(), 443));
        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params,
                schemeRegistry);
        HttpClient httpClient = new DefaultHttpClient(ccm, params);

        // Download the file
        String url = "http://www.eclipse.org/downloads/download.php?file=/eclipse/downloads/drops/R-3.3.1.1-200710231652/eclipse-CVS-Client-SDK-3.3.1.1.zip&url=http://download.eclipse.org/eclipse/downloads/drops/R-3.3.1.1-200710231652/eclipse-CVS-Client-SDK-3.3.1.1.zip&mirror_id=1";
        HttpGet httpget = new HttpGet(url);
        List defaultHeaders = new ArrayList(1);
        defaultHeaders.add(new BasicHeader("Range", "bytes=100-"));
        httpget.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, defaultHeaders);
        HttpResponse response = httpClient.execute(httpget);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_PARTIAL_CONTENT) {
            throw new IllegalArgumentException("Invalid reponse!");
        }

    }
}
