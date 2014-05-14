package org.jboss.keynote2014.twitter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Handler;

import twitter4j.Status;
import twitter4j.User;

public class TwitterConvertBean
{
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private String id;
    private String date;
    private String name;
    private String screenName;
    private String text;
    private String tag;
    
    @Handler
    public Map<String, String> process(final Status status)
    {
        final User user = status.getUser();
        final String content = status.getText();
        
        final Map<String, String> map = new HashMap<String, String>();
        map.put(id, String.valueOf(status.getId()));
        synchronized(dateFormat) {
            map.put(this.date, dateFormat.format(status.getCreatedAt()));
        }
        map.put(name, user.getName());
        map.put(screenName, user.getScreenName());
        map.put(text, content);
        map.put(tag, content.contains("demoup") ? "demoup" : "demodown");

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
