package org.jboss.keynote2014.json.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class JSONClientUtils
{
    private static final String CHARSET = "charset=";
    
    public static JSONResponse execute(final String uri, final Map<String, String> headers)
        throws ClientProtocolException, IOException
    {
        final HttpResponse httpResponse = HttpClientUtils.getHttpClientUtils().get(uri, headers);
        return getJSONResponse(httpResponse);
    }
    
    public static JSONResponse execute(final String uri, final Map<String, String> headers, final JSONObject payload)
        throws ClientProtocolException, IOException
    {
        final StringWriter sw = new StringWriter();
        payload.writeJSONString(sw);
        final HttpResponse httpResponse = HttpClientUtils.getHttpClientUtils().post(uri, headers, sw.toString());
        return getJSONResponse(httpResponse);
    }
    
    public static JSONResponse execute(final String uri, final Map<String, String> headers, final Map<String, String> parameters)
        throws ClientProtocolException, IOException
    {
        final List<NameValuePair> formParameters = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry: parameters.entrySet()) {
            formParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        final HttpResponse httpResponse = HttpClientUtils.getHttpClientUtils().post(uri, headers, formParameters);
        return getJSONResponse(httpResponse);
    }
        
    private static JSONResponse getJSONResponse(final HttpResponse httpResponse)
        throws ClientProtocolException, IOException
    {
        final int statusCode = httpResponse.getStatusLine().getStatusCode();
        final HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            final InputStreamReader isr = new InputStreamReader(entity.getContent(), getCharset(entity.getContentType().getValue()));
            try {
                final Object response = JSONValue.parse(isr);
                if (response instanceof JSONObject) {
                    return new JSONResponse(statusCode, (JSONObject) response);
                } else if (response instanceof JSONArray) {
                    return new JSONResponse(statusCode, (JSONArray) response);
                } else {
                    throw new IOException("Unexpected response : " + response);
                }
            } finally {
                isr.close();
            }
        } else {
            return new JSONResponse(statusCode);
        }
    }

    private static String getCharset(final String contentType)
    {
        final int start = contentType.indexOf(CHARSET);
        if (start > -1) {
            final int realStart = start + CHARSET.length();
            final int end = contentType.indexOf(';', realStart);
            if (end == -1) {
                return contentType.substring(realStart);
            } else {
                return contentType.substring(realStart, end);
            }
        } else {
            return "ISO-8859-1";
        }
    }
}
