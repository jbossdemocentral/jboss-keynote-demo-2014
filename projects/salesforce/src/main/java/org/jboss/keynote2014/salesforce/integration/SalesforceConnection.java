package org.jboss.keynote2014.salesforce.integration;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.LoggingLevel;
import org.apache.camel.util.CamelLogger;
import org.apache.http.HttpStatus;
import org.jboss.keynote2014.json.utils.JSONClientUtils;
import org.jboss.keynote2014.json.utils.JSONResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SalesforceConnection
{
    // Login Endpoint.  Note, this is different from the standard endpoint
    private static final String LOGIN_ENDPOINT = "https://test.salesforce.com/services/oauth2/token";
    // REST API path
    private static final String REST_API_PATH = "/services/data/v29.0";
    // Login credentials
    private static final String CLIENT_ID = "3MVG98RqVesxRgQ4YVRH2I7LC4WQLGNrrM15E.Ker1wEbuAQgntaPWOULipv9V6geqrEWoDdtuiU5mwRJGEWw";
    private static final String CLIENT_SECRET = "9153398612327264512";
    private static final String USERNAME = "kconner@redhat.com.demo";
    private static final String PASSWORD = "dFH4rm9LZg";
    
    // JSON keys
    private static final String INSTANCE_URL = "instance_url";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String ERROR_DESCRIPTION = "error_description";
    private static final String TOTAL_SIZE = "totalSize";
    private static final String RECORDS = "records";
    private static final String EMAIL = "Email";
    private static final String MOBILE_PHONE = "MobilePhone";
    
    // Authorization
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    
    private final CamelLogger logger = new CamelLogger(SalesforceConnection.class.getCanonicalName(), LoggingLevel.DEBUG);
    
    private final String instanceURL;
    private final String accessToken;
    
    public SalesforceConnection()
        throws IOException
    {
        logger.log("Logging in to Salesforce");

        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("grant_type", "password");
        parameters.put("client_id", CLIENT_ID);
        parameters.put("client_secret", CLIENT_SECRET);
        parameters.put("username", USERNAME);
        parameters.put("password", PASSWORD);
        
        final JSONResponse response = JSONClientUtils.execute(LOGIN_ENDPOINT, null, parameters);
        if (response.getStatusCode() == HttpStatus.SC_OK) {
            final JSONObject jsonResponse = response.getPayloadAsJSONObject();
            instanceURL = (String) jsonResponse.get(INSTANCE_URL) + REST_API_PATH;
            accessToken = (String) jsonResponse.get(ACCESS_TOKEN);
        } else {
            logError(response, "login");
            throw getErrorException(response, "login");
        }
    }
    
    public SalesforceUser queryContact(final String twitter)
        throws IOException
    {
        logger.log("queryContact: Querying contact for " + twitter);
        
        final String uri = generateURI("/query?q=" + URLEncoder.encode("SELECT Email,MobilePhone from Contact where Department='" + twitter + "'", "UTF-8"));
        final JSONResponse response = JSONClientUtils.execute(uri, getHeaders());
        
        logger.log("queryContact: Status Code " + response.getStatusCode());
        
        if (response.getStatusCode() == HttpStatus.SC_OK) {
            final JSONObject jsonObject = response.getPayloadAsJSONObject();
            final long totalSize = (Long)jsonObject.get(TOTAL_SIZE);
            if (totalSize > 0) {
                final JSONArray records = (JSONArray)jsonObject.get(RECORDS);
                final JSONObject contact = (JSONObject)records.get(0);
                logger.log("queryContact: contact is " + contact);
                return new SalesforceUser((String)contact.get(EMAIL), (String)contact.get(MOBILE_PHONE));
            } else {
                logger.log("queryContact: no contact found");
                return null;
            }
        } else {
            logError(response, "queryContact");
            throw new IOException("Failed to query contact information");
        }
    }

    public JSONObject getObjectInformation()
        throws IOException
    {
        final String uri = generateURI("/query?q=" + URLEncoder.encode("SELECT FirstName,LastName,Department,Email,MobilePhone from Contact", "UTF-8"));
        final JSONResponse response = JSONClientUtils.execute(uri, getHeaders());
        if (response.getStatusCode() == HttpStatus.SC_OK) {
            return response.getPayloadAsJSONObject();
        } else {
            throw getErrorException(response, "retrieve Object Information");
        }
    }

    private Map<String, String> getHeaders()
    {
        final Map<String, String> headers = new HashMap<String, String>();
        headers.put(AUTHORIZATION, BEARER + accessToken);
        return headers;
    }

    private String generateURI(final String uri)
    {
        return (uri == null ? instanceURL : instanceURL + uri); 
    }

    private void logError(final JSONResponse response, final String method)
    {
        final JSONObject obj = response.getPayloadAsJSONObject();
        final Object value;
        if (obj != null) {
            value = obj;
        } else {
            value = response.getPayloadAsJSONArray();
        }
        logger.log("Unexpected response:" + value);
    }

    private IOException getErrorException(final JSONResponse response, final String task)
    {
        final JSONObject jsonResponse = response.getPayloadAsJSONObject();
        final String description = (String) jsonResponse.get(ERROR_DESCRIPTION);
        return new IOException("Failed to " + task + ": " + description);
    }
    
    public static void main(final String[] args)
        throws IOException
    {
        final SalesforceConnection connection = new SalesforceConnection();
        System.out.println(connection.getObjectInformation());
        System.out.println(connection.queryContact("jbwdemo"));
        System.out.println(connection.queryContact("broken"));
    }
}
