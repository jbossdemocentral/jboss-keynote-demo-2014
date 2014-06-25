package org.jboss.keynote2014.salesforce;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Message;
import org.jboss.keynote2014.salesforce.model.TwitterPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitterProcessor {
	private static Logger logger = LoggerFactory.getLogger(TwitterProcessor.class);
	@Handler
    public String process(final Exchange exchange)throws IOException, XMLStreamException{
		String id = ""; 
		final Message message = exchange.getIn();
		 TwitterPojo twitterInfo = message.getBody(TwitterPojo.class);
		 logger.info("message:["+message+"]");
		 if(twitterInfo != null){
			 
			 logger.info("=======TWITTER POJO STARTS========");
			 logger.info("ID:"+twitterInfo.getId());
			 logger.info("NAME:"+twitterInfo.getName());
			 logger.info("SCREEN NAME:"+twitterInfo.getScreenName());
			 logger.info("TEXT:"+twitterInfo.getText());
			 logger.info("DATE:"+twitterInfo.getDate());
			 logger.info("==================================");
			 
			 id = twitterInfo.getId();
		 }
		 
		
		return id;
	}
	
	public void sayHi(){
		 logger.info("Hi Hi Hi~~~~~~~");
	}
}
