package org.jboss.keynote2014.twitter.loadtest;

import java.util.Date;
import java.util.Random;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class LoadTest implements Runnable
{
    private static final String LOAD_TEST_KEY = "uFJhpbzHQkQ5amR34BYyHrIr0";
    private static final String LOAD_TEST_SECRET = "QY8QgdkJ1GTRZOOhbSBUBLhNRYQWlHHS55Ms4Egp8edFEUWcO6";
    
    private static final String HASHTAG_GOOD = "#demoup";
    private static final String HASHTAG_BAD = "#demodown";
    
    private static final String[] IDS = {
        "demacc1",
        "demacc2",
        "demacc3",
        "demacc4",
        "demacc5",
    };
    
    private static final String[] TOKENS = {
        "2422702519-HS4W5BuYuIOWmmCVKnv3PxZZzOo82sWajUxVCvw",
        "2422698002-QNOZrawuvoWfe3tgKAvDhgpL4p87oNq4PJISD7K",
        "2422700310-bcxDosuvYfYOvdJDz4sqV8MRCQ2vh8Bcmr8snUv",
        "2422714298-1qBy5ycQ81xTipsDccWLpfzK0Ml5XHu9pj2FMpX",
        "2422714820-OVErU7Hc8Bg1W56OjhdWpmUlVAOyCbd2QM5yCkj"
    };
    
    private static final String[] SECRETS = {
        "Jli9jS4NiP9MnU1qhgPx7ifq2obk2rdKNWpv4uBcd3xBD",
        "eTrXmAHcoErRan9VHSDtA2m0H6DFHJBrAjGuHgZIXQKlJ",
        "cttJr7eIfFIOIfaS1mbuSZ0myvRrA9tsohDLKWU6qi7ua",
        "iAqoT8CJ7yngY6g9o68rfVpDhMxgh3W5tLlRowrz4p50h",
        "9fIu3iyTeIpJuOYcCo1BuexoSf1woGiQeFQLILAJJkTqS"
    };
    
    private final Twitter twitter ;
    private final String name ;
    private final int clientTweets;
    private final long delay;
    private final Random random;
    
    private LoadTest(final String name, final String token, final String secret, final int clientTweets, final long delay)
    {
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(LOAD_TEST_KEY, LOAD_TEST_SECRET);
        twitter.setOAuthAccessToken(new AccessToken(token, secret));
        this.name = name;
        this.clientTweets = clientTweets;
        this.delay = delay;
        this.random = new Random();
    }

    @Override
    public void run()
    {
        long maxInvocationTime = 0;
        long totalInvocationTime = 0;
        try {
            for(int count = 0 ; count < clientTweets ; count++) {
                final String tag = random.nextBoolean() ? HASHTAG_GOOD : HASHTAG_BAD;
                final long start = System.currentTimeMillis();
                twitter.updateStatus(tag + " sent by " + name + " at " + new Date());
                final long end = System.currentTimeMillis();
                
                final long currentInvocationTime = end-start;
                if (currentInvocationTime > maxInvocationTime) {
                    maxInvocationTime = currentInvocationTime;
                }
                totalInvocationTime += currentInvocationTime;
                
                final long currentDelay = delay - end + start;
                if (currentDelay > 0) {
                    try {
                        Thread.sleep(currentDelay);
                    } catch (final InterruptedException ie) {} // ignoring
                }
            }
        } catch (final TwitterException te) {
            System.err.println("Client " + name + " terminating early with " + te.getMessage());
            te.printStackTrace(System.err);
        }
        
        System.out.println("Client " + name + " leaves with maximum invocation time of " + maxInvocationTime + " and average invocation time of " + totalInvocationTime/clientTweets);
    }

    // <NumTweets> <NumSeconds>
    
    public static void main(final String[] args) throws Exception
    {
        if (args.length != 2) {
            System.err.println("Usage: java " + LoadTest.class.getName() + " <NumTweets> <NumSeconds>");
        } else {
            final int numTweets = Integer.parseInt(args[0]);
            final long numSeconds = Long.parseLong(args[1]);
            
            final int numClients = IDS.length;
//            final int numClients = 1;
            
            final int totalTweets = ((numTweets+numClients-1) / numClients) * numClients;
            
            final int waitingTweets = totalTweets - numClients;
            final long delay = waitingTweets > 0 ? (1000*numSeconds*numClients)/(waitingTweets) : 0;
            
            System.out.println("Sending " + totalTweets + " tweets with a period of " + delay + " milliseconds");
            
            final Thread[] threads = new Thread[numClients];
            final int clientTweets = totalTweets/numClients;
            
            for (int count = 0 ; count < numClients ; count++) {
                threads[count] = new Thread(new LoadTest(IDS[count], TOKENS[count], SECRETS[count], clientTweets, delay));
            }
            
            for (int count = 0 ; count < numClients ; count++) {
                threads[count].start();
            }
            
            for (int count = 0 ; count < numClients ; count++) {
                threads[count].join();
            }
        }
    }
}
