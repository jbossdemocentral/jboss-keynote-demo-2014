package org.jboss.keynote2014.salesforce;

import java.io.IOException;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.util.CamelLogger;
import org.apache.http.client.ClientProtocolException;
import org.jboss.keynote2014.salesforce.integration.SalesforceConnection;
import org.jboss.keynote2014.salesforce.integration.SalesforceUser;

public class Salesforce
{
    private final CamelLogger logger = new CamelLogger(Salesforce.class.getCanonicalName(), LoggingLevel.INFO);
    
    private String salesforce;
    private String screenName;
    private String email;
    private String mobilePhone;
    private String dbTrue;
    private String dbFalse;
    
    @Handler
    public void process(final Exchange exchange)
        throws ClientProtocolException, IOException
    {
        final Message message = exchange.getIn();
        final Map<String, String> map = (Map<String, String>) message.getBody();

        final String screenName = map.get(this.screenName);
        final SalesforceConnection connection = new SalesforceConnection();
        final SalesforceUser salesforceUser = connection.queryContact(screenName);
        
        if (salesforceUser != null) {
            map.put(email, salesforceUser.getEmail());
            map.put(mobilePhone, salesforceUser.getMobilePhone());
            map.put(salesforce, dbTrue);
            logger.log("User: " + screenName + ", email: " + salesforceUser.getEmail() + ", mobile phone: " + salesforceUser.getMobilePhone());
        } else {
            map.put(salesforce, dbFalse);
            logger.log("No salesforce user found for screen name: " + screenName);
        }
    }
    
    public void setSalesforce(final String salesforce)
    {
        this.salesforce = salesforce;
    }
    
    public void setScreenName(final String screenName)
    {
        this.screenName = screenName;
    }
    
    public void setEmail(final String email)
    {
        this.email = email;
    }
    
    public void setMobilePhone(final String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public void setDbTrue(final String dbTrue)
    {
        this.dbTrue = dbTrue;
    }

    public void setDbFalse(final String dbFalse)
    {
        this.dbFalse = dbFalse;
    }
}
