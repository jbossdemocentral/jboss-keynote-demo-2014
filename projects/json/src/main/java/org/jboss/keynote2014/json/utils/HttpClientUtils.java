package org.jboss.keynote2014.json.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;


public class HttpClientUtils
{
    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String BASIC = "Basic";
    
    private static final int MAX_CONNECTIONS = 200;
    private static final int TIMEOUT = 30 * 1000;
    
    private final HttpClient httpClient;
    
    private HttpClientUtils()
    {
        final HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT);
        final PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS);
        connectionManager.setMaxTotal(MAX_CONNECTIONS);
        httpClient = new DefaultHttpClient(connectionManager, params);
    }
    
    public HttpResponse post(final String uri, final Map<String, String> headers)
        throws ClientProtocolException, IOException
    {
        final HttpPost post = getPost(uri, headers);
        return httpClient.execute(post);
    }
    
    public HttpResponse post(final String uri, final Map<String, String> headers, final String payload)
        throws ClientProtocolException, IOException
    {
        final HttpPost post = getPost(uri, headers);
        post.setEntity(new StringEntity(payload));
        return httpClient.execute(post);
    }
    
    public HttpResponse post(final String uri, final Map<String, String> headers, final List<? extends NameValuePair> parameters)
        throws ClientProtocolException, IOException
    {
        final HttpPost post = getPost(uri, headers);
        post.setEntity(new UrlEncodedFormEntity(parameters));
        return httpClient.execute(post);
    }

    public HttpResponse get(final String uri, final Map<String, String> headers)
        throws ClientProtocolException, IOException
    {
        final HttpGet get = getGet(uri, headers);
        return httpClient.execute(get);
    }
    
    public String generateAuthorisation(final String username, final String password){
        final String authorisation = username + ":" + password;
        return new String(Base64.encodeBase64(authorisation.getBytes()));
    }
    
    private HttpPost getPost(final String uri, final Map<String, String> headers)
    {
        final HttpPost post = new HttpPost(uri);
        if ((headers != null) && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry: headers.entrySet()) {
                final String name = entry.getKey();
                final String value = entry.getValue();
                post.setHeader(name, value);
            }
        }
        return post;
    }
    
    private HttpGet getGet(final String uri, final Map<String, String> headers)
    {
        final HttpGet get = new HttpGet(uri);
        if ((headers != null) && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry: headers.entrySet()) {
                final String name = entry.getKey();
                final String value = entry.getValue();
                get.setHeader(name, value);
            }
        }
        return get;
    }

    public String readContent(final InputStream is)
        throws IOException
    {
        final StringBuilder sb = new StringBuilder();
        final byte[] buffer = new byte[256];
        while(true) {
            final int count = is.read(buffer);
            if (count == -1) {
                break;
            } else {
                sb.append(new String(buffer, 0, count));
            }
        }
        return sb.toString();
    }

    private static final HttpClientUtils SINGLETON = new HttpClientUtils() ;

    public static HttpClientUtils getHttpClientUtils()
    {
        return SINGLETON;
    }
}
