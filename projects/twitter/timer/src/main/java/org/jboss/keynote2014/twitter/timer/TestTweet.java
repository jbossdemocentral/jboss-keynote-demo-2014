package org.jboss.keynote2014.twitter.timer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.camel.Handler;
import org.apache.camel.LoggingLevel;
import org.apache.camel.util.CamelLogger;

public class TestTweet
{
    private final CamelLogger logger = new CamelLogger(TestTweet.class.getCanonicalName(), LoggingLevel.INFO);
    
    private final Random random = new Random();
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private final String[] names = {
            "Test Name 1", 
            "Test Name 2", 
            "Test Name 3", 
            "Test Name 4", 
            "Test Name 5", 
            "Test Name 6", 
            "Test Name 7", 
            "Test Name 8", 
            "Test Name 9", 
            "Test Name 10", 
            "推特",
    } ;
    private final String[] screenNames = {
            "testScreenName1", 
            "testScreenName2", 
            "testScreenName3", 
            "testScreenName4", 
            "testScreenName5", 
            "testScreenName6", 
            "testScreenName7", 
            "testScreenName8", 
            "testScreenName9", 
            "testScreenName10",
            "推特"
    } ;
    private final String[] messages = {
            "Example text message for testing - 1", 
            "Example text message for testing - 2", 
            "Example text message for testing - 3", 
            "Example text message for testing - 4", 
            "Example text message for testing - 5", 
            "Example text message for testing - 6", 
            "Example text message for testing - 7", 
            "Example text message for testing - 8", 
            "Example text message for testing - 9", 
            "Example text message for testing - 10",
            "Chinese characters 推特"
    } ;
    
    private String id;
    private String date;
    private String name;
    private String screenName;
    private String text;
    private String tag;

    @Handler
    public Map<String, String> process()
    {
        final Map<String, String> map = new HashMap<String, String>();
        
        final int idx = random.nextInt(names.length);
        
        final String id = String.valueOf(System.currentTimeMillis()) + String.valueOf(random.nextInt(100000));
        final String name = names[idx];
        final Date date = new Date();
        final String screenName = screenNames[idx];
        final String message = messages[idx];
        final String tag = random.nextBoolean() ? "demoup" : "demodown";
        
        
        map.put(this.id, id);
        synchronized(dateFormat) {
            map.put(this.date, dateFormat.format(date));
        }
        map.put(this.name, name);
        map.put(this.screenName, screenName);
        map.put(this.text, message);
        map.put(this.tag, tag);
        
        logger.log("Sending test tweet from " + name + ", " + screenName + " : " + tag + ", " + message);
        
        return map;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public void setDate(final String date)
    {
        this.date = date;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public void setScreenName(final String screenName)
    {
        this.screenName = screenName;
    }

    public void setText(final String text)
    {
        this.text = text;
    }

    public void setTag(final String tag)
    {
        this.tag = tag;
    }
}
