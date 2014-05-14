package org.jboss.keynote2014.twilio;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.jboss.keynote2014.json.utils.HttpClientUtils;
import org.jboss.keynote2014.json.utils.JSONClientUtils;
import org.jboss.keynote2014.json.utils.JSONResponse;

public class TwilioClient
{
    // Login credentials
    private static final String ACCOUNT_SID = "ACa0b2ffa434f526850c210ff275e92948";
    private static final String AUTH_TOKEN = "fb5d0e5106b6844fc4feac0ce3fd1f40";
    // SMS send message endpoint
    private static final String ENDPOINT = "https://api.twilio.com/2010-04-01/Accounts/" + ACCOUNT_SID + "/Messages.json";
    // Twilio Mobile Numbers
    private static final String[] MOBILE_NUMBERS = {
        "+15105009629",
        "+15105009641",
        "+14157023103",
        "+14157023104",
        "+14157023116",
        "+14157023118",
        "+14157023129",
        "+14157023130",
        "+14157023133",
        "+14157023153",
    };
    private static final int NUM_MOBILE_NUMBERS = MOBILE_NUMBERS.length;
    
    // JSON keys
    private static final String TO = "To";
    private static final String FROM = "From";
    private static final String BODY = "Body";
    // Mobile Number counter
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    
    public boolean sendSMS(final String to, final String body)
        throws ClientProtocolException, IOException
    {
        // Truncate the message to 140 characters
        final String message;
        if (body.length() > 140) {
            message = body.substring(0, 140) + " ...";
        } else {
            message = body;
        }
        
        final String authorisation = HttpClientUtils.getHttpClientUtils().generateAuthorisation(ACCOUNT_SID, AUTH_TOKEN);
        
        final Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpClientUtils.AUTHORIZATION, HttpClientUtils.BASIC + " " + authorisation);
        
        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(TO, to);
        final int next = (COUNTER.getAndAdd(1) % NUM_MOBILE_NUMBERS);
        
        parameters.put(FROM, MOBILE_NUMBERS[next]);
        parameters.put(BODY,  message);
        
        JSONResponse execute = JSONClientUtils.execute(ENDPOINT, headers, parameters);
        if( execute.getStatusCode() != HttpStatus.SC_CREATED) {
            System.out.println("Failed sending SMS to " + to);
            System.out.println("  Message: " + execute.getPayloadAsJSONObject().get("message"));
            System.out.println("   Status: " + execute.getPayloadAsJSONObject().get("status"));
            System.out.println("More info: " + execute.getPayloadAsJSONObject().get("more_info"));
            System.out.println("     Code: " + execute.getPayloadAsJSONObject().get("code"));
            return  false;
        } else {
            System.out.println("Message sent to " + to);
            return true;
        }
    }


    public static void main(final String[] args)
        throws ClientProtocolException, IOException
    {
        final TwilioClient client = new TwilioClient();
        final String[] text = { "First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth", "Ninth", "Tenth" };
        for (int count = 0 ; count < text.length ; count++) {
            if (client.sendSMS("4153356817", text[count] + " test message sent from all twilio numbers")) {
                System.out.println("Success");
            } else {
                System.out.println("Failed to send SMS");
            }
        }
    }
}
