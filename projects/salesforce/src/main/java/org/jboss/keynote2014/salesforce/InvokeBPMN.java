package org.jboss.keynote2014.salesforce;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.stream.XMLStreamException;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.util.CamelLogger;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.jboss.keynote2014.json.utils.HttpClientUtils;
import org.jboss.keynote2014.salesforce.integration.BPMNUtils;

public class InvokeBPMN
{
    private final CamelLogger logger = new CamelLogger(InvokeBPMN.class.getCanonicalName(), LoggingLevel.INFO);
    
    private String salesforce;
    
    private String deploymentID;
    private String deploymentVersion;
    private String processID;
    private String username;
    private String password;
    private String baseURI;
    
    private String screenName;
    private String name;
    private String email;
    private String mobilePhone;
    private String text;

    private String dbTrue;
    
    @Handler
    public void process(final Exchange exchange)
        throws IOException, XMLStreamException
    {
        final Message message = exchange.getIn();
        final Map<String, String> map = (Map<String, String>) message.getBody();
        final boolean salesforce = dbTrue.equals(map.get(this.salesforce));
        if (salesforce) {
            logger.log("Creating BPMN process");
    
            final String screenName = map.get(this.screenName);
            final String name = map.get(this.name);
            final String email = map.get(this.email);
            final String mobilePhone = map.get(this.mobilePhone);
            final String text = map.get(this.text);
    
            createBPMNProcess(screenName, name, email, mobilePhone, text);
        } else {
            logger.log("No salesforce account discovered so ignoring BPMN process");
        }
    }
    
    private void createBPMNProcess(final String screenName, final String name, final String email,
        final String mobilePhone, final String text)
        throws XMLStreamException, IOException
    {
        final BPMNUtils bpmnUtils = BPMNUtils.getBPMNUtils();
        final String payload = bpmnUtils.startProcess(deploymentID, deploymentVersion, processID,
            screenName, name, email, mobilePhone, text);
        final String uri = bpmnUtils.getRuntimExecuteURI(baseURI, deploymentID);

        final String authorisation = HttpClientUtils.getHttpClientUtils().generateAuthorisation(username, password);
        
        final Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpClientUtils.AUTHORIZATION, HttpClientUtils.BASIC + " " + authorisation);
        headers.put(HttpClientUtils.CONTENT_TYPE, "application/xml");

        final HttpResponse post = HttpClientUtils.getHttpClientUtils().post(uri, headers, payload);
        final int statusCode = post.getStatusLine().getStatusCode();
        final InputStream is = post.getEntity().getContent();
        try {
            if (HttpStatus.SC_OK == statusCode) {
                logger.log("BPMN Process Created");
            } else {
                final String content = HttpClientUtils.getHttpClientUtils().readContent(is);
                logger.log("Failed to Create BPMN Process for tweet, status code: " + statusCode + ", " + content);
            }
        } finally {
            is.close();
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

    public void setName(final String name)
    {
        this.name = name;
    }
    
    public void setEmail(final String email)
    {
        this.email = email;
    }
    
    public void setMobilePhone(final String mobilePhone)
    {
        this.mobilePhone = mobilePhone;
    }

    public void setText(final String text)
    {
        this.text = text;
    }

    public void setDeploymentID(final String deploymentID)
    {
        this.deploymentID = deploymentID;
    }

    public void setDeploymentVersion(final String deploymentVersion)
    {
        this.deploymentVersion = deploymentVersion;
    }

    public void setProcessID(final String processID)
    {
        this.processID = processID;
    }

    public void setUsername(final String username)
    {
        this.username = username;
    }

    public void setPassword(final String password)
    {
        this.password = password;
    }

    public void setBaseURI(final String baseURI)
    {
        this.baseURI = baseURI;
    }

    public void setDbTrue(final String dbTrue)
    {
        this.dbTrue = dbTrue;
    }
    
    public static void main(final String args[])
        throws Exception
    {
        final ExecutorService executor = Executors.newFixedThreadPool(30);
        final AtomicInteger counter = new AtomicInteger(0);
        
        for(int count = 0 ; count < 500 ; count++)
        {
            executor.execute(new Runnable() {
                public void run() {
                    final InvokeBPMN bpmn = new InvokeBPMN();
                    
                    bpmn.setDeploymentID("org.jboss.demo:customer-follow-up:1.0");
                    bpmn.setDeploymentVersion("1");
                    bpmn.setProcessID("CustomerFollowUp");
                    bpmn.setUsername("erics");
                    bpmn.setPassword("bpmsuite");
                    bpmn.setBaseURI("http://localhost:8080/business-central/");
                    
                    final long start = System.currentTimeMillis();
                    try {
                        bpmn.createBPMNProcess("jbwdemo", "Demo Twitter Account", "a@b.c.com", "555-555-5555", "text from tweet #jbossrocks");
                    } catch (final Exception ex) {
                        ex.printStackTrace(System.err);
                    } finally {
                        final long end = System.currentTimeMillis();
                        final int nextCounter = counter.incrementAndGet() ;
                        System.out.println("Execution " + nextCounter + " took " + (end-start) + " ms");
                    }
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.SECONDS);
    }
}
