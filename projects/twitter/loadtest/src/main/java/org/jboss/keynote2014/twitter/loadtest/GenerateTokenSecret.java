package org.jboss.keynote2014.twitter.loadtest;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class GenerateTokenSecret
{
    private static final String LOAD_TEST_KEY = "uFJhpbzHQkQ5amR34BYyHrIr0";
    private static final String LOAD_TEST_SECRET = "QY8QgdkJ1GTRZOOhbSBUBLhNRYQWlHHS55Ms4Egp8edFEUWcO6";
    
    public static void main(final String[] args) throws Exception
    {
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(LOAD_TEST_KEY, LOAD_TEST_SECRET);
        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (null == accessToken) {
          System.out.println("Open the following URL and grant access to your account:");
          System.out.println(requestToken.getAuthorizationURL());
          System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
          String pin = br.readLine();
          try{
             if(pin.length() > 0){
               accessToken = twitter.getOAuthAccessToken(requestToken, pin);
             }else{
               accessToken = twitter.getOAuthAccessToken();
             }
          } catch (TwitterException te) {
            if(401 == te.getStatusCode()){
              System.out.println("Unable to get the access token.");
            }else{
              te.printStackTrace();
            }
          }
        }
        //persist to the accessToken for future reference.
        System.out.println("ID: " + twitter.verifyCredentials().getId() + " token: " + accessToken.getToken() + " secret: " + accessToken.getTokenSecret());
    }
}
