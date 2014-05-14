package org.jboss.keynote2014.salesforce.integration;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class BPMNUtils
{
    private static final BPMNUtils SINGLETON = new BPMNUtils();
    
    private static final String ELEM_COMMAND_REQUEST = "command-request";
    private static final String ELEM_DEPLOYMENT_ID = "deployment-id";
    private static final String ELEM_VER = "ver";
    private static final String ELEM_START_PROCESS = "start-process";
    private static final String ELEM_PARAMETER = "parameter";
    private static final String ELEM_ITEM = "item";
    private static final String ELEM_VALUE = "value";
    private static final String ELEM_CUSTOMER_COMMENTS = "customerComments";
    private static final String ELEM_EMAIL = "email";
    private static final String ELEM_NAME = "name";
    private static final String ELEM_PHONE = "phone";
    private static final String ELEM_TWEETER_ID = "tweeterId";
    private static final String ATTR_PROCESS_ID = "processId";
    private static final String ATTR_KEY = "key";
    
    private static final String URI_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String URI_XS = "http://www.w3.org/2001/XMLSchema";
    
    private final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
    private final Random random = new Random();
    
    public static BPMNUtils getBPMNUtils()
    {
        return SINGLETON;
    }

    public String startProcess(final String deploymentID, final String deploymentVersion,
        final String processID, final String screenName, final String name, final String email,
        final String mobilePhone, final String text)
        throws XMLStreamException
    {
        final StringWriter sw = new StringWriter();
        final XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(sw);
        
        streamWriter.writeStartDocument();
        
        streamWriter.writeStartElement(ELEM_COMMAND_REQUEST);
        
        writeTextElement(streamWriter, ELEM_DEPLOYMENT_ID, deploymentID);
        writeTextElement(streamWriter, ELEM_VER, deploymentVersion);
        
        streamWriter.writeStartElement(ELEM_START_PROCESS);
        streamWriter.writeAttribute(ATTR_PROCESS_ID, processID);
        
        streamWriter.writeStartElement(ELEM_PARAMETER);
        
        writeStartItem(streamWriter, "initialDueDate", null);
        streamWriter.writeCharacters("P" + random.nextInt(30) + "d");
        writeEndItem(streamWriter);
        
        writeStartItem(streamWriter, "customer", "customer");
        writeTextElement(streamWriter, ELEM_CUSTOMER_COMMENTS, text);
        writeTextElement(streamWriter, ELEM_EMAIL, email);
        writeTextElement(streamWriter, ELEM_NAME, name);
        writeTextElement(streamWriter, ELEM_PHONE, mobilePhone);
        writeTextElement(streamWriter, ELEM_TWEETER_ID, screenName);
        writeEndItem(streamWriter);
        
        streamWriter.writeEndElement(); // PARAMETER
        streamWriter.writeEndElement(); // START_PROCESS
        streamWriter.writeEndElement(); // COMMAND_REQUEST
        
        streamWriter.flush();
        return sw.toString();
    }

    public String getRuntimExecuteURI(final String baseURI, final String deploymentID)
        throws UnsupportedEncodingException
    {
        return baseURI + (baseURI.endsWith("/") ? "" : "/") + "rest/runtime/" + deploymentID + "/execute";
    }
    
    private void writeTextElement(final XMLStreamWriter streamWriter, final String elementName, final String content)
        throws XMLStreamException
    {
        streamWriter.writeStartElement(elementName);
        if (content != null) {
            streamWriter.writeCharacters(content);
        }
        streamWriter.writeEndElement();
    }
    
    private void writeStartItem(final XMLStreamWriter streamWriter, final String itemKey, final String type)
        throws XMLStreamException
    {
        streamWriter.writeStartElement(ELEM_ITEM);
        streamWriter.writeAttribute(ATTR_KEY, itemKey);
        
        streamWriter.writeStartElement(ELEM_VALUE);
        streamWriter.writeNamespace("xsi", URI_XSI);
        if (type == null) {
            streamWriter.writeNamespace("xs", URI_XS);
            streamWriter.writeAttribute(URI_XSI, "type", "xs:string");
        } else {
            streamWriter.writeAttribute(URI_XSI, "type", type);
        }
    }
    
    private void writeEndItem(final XMLStreamWriter streamWriter)
        throws XMLStreamException
    {
        streamWriter.writeEndElement(); // VALUE
        streamWriter.writeEndElement(); // ITEM
    }
}

