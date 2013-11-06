package org.hoteia.cas;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HttpClientParserUtil {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0";

    private static PoolingClientConnectionManager manager = createPoolingClientConnectionManager();
    private static ThreadLocal<DefaultHttpClient> httpClient = new ThreadLocal<DefaultHttpClient>() {
        @Override
        protected DefaultHttpClient initialValue() {
            return getNewHttpClient();
        }
    };

    public static DefaultHttpClient getNewHttpClient() {
        try {
            return createDefaultHttpClient(manager);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new DefaultHttpClient();
    }

    private static PoolingClientConnectionManager createPoolingClientConnectionManager() {
        try {
            PoolingClientConnectionManager manager = new PoolingClientConnectionManager();
            manager.setDefaultMaxPerRoute(10);
            return manager;
        } catch (Exception ignored) {
        }

        return new PoolingClientConnectionManager();

    }

    private static DefaultHttpClient createDefaultHttpClient(PoolingClientConnectionManager manager) {
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient(manager);
        defaultHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 90000);
        defaultHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 90000);
        addProxyIfNeeded(defaultHttpClient);
        return defaultHttpClient;
    }

    private static void addProxyIfNeeded(DefaultHttpClient defaultHttpClient) {
        String proxyPort = System.getProperty("http.proxyPort");
        String proxyHost = System.getProperty("http.proxyHost");

        if (proxyPort != null && proxyHost != null) {
            configureClientToUseProxy(defaultHttpClient, proxyPort, proxyHost);
        }
    }

    private static void configureClientToUseProxy(DefaultHttpClient defaultHttpClient, String proxyPort, String proxyHost) {
        HttpHost proxy = new HttpHost(proxyHost, Integer.parseInt(proxyPort));
        defaultHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
    }

    public static HttpResponse postDataToUrl(String url, Map<String, String> data) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setEntity(new UrlEncodedFormEntity(toNameValuePairs(data), Charset.forName("UTF-8")));
        return execute(post);
    }

    private static List<NameValuePair> toNameValuePairs(Map<String, String> data) {
        List<NameValuePair> params = new ArrayList<NameValuePair>(data.size());
        for(Map.Entry<String, String> datum: data.entrySet()) {
            params.add(new BasicNameValuePair(datum.getKey(), datum.getValue()));
        }
        return params;
    }

    public static HttpResponse execute(HttpUriRequest request) throws IOException {
         return httpClient.get().execute(request);
    }
}
