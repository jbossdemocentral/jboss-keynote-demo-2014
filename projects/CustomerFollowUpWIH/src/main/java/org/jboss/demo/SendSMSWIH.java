package org.jboss.demo;

import org.drools.persistence.jpa.JpaPersistenceContextManager;
import org.jboss.keynote2014.twilio.TwilioClient;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;

public class SendSMSWIH implements WorkItemHandler {
    private static final String DEFAULT_SMS_MESSAGE = "Thank you for your feedback. Your tracking number is #PID#.";
    private KieSession kSession;

    public SendSMSWIH(KieSession kSession) {
        this.kSession = kSession;
    }

    public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
        System.out.println("**************************************************************");
        System.out.println("Executing send SMS WIH : ");
        System.out.println("**************************************************************");


        try {
            WorkflowProcessInstance processInstance = (WorkflowProcessInstance) kSession.getProcessInstance(wi.getProcessInstanceId());
            if (processInstance != null) {

                Object customer = processInstance.getVariable("customer");
                if (customer != null) {
                    String name = getValueFromObject(customer, "Name");  // Hack to avoid classloading issues (probably fixed now)
                    String phone = getValueFromObject(customer, "Phone"); // Hack to avoid classloading issues (probably fixed now)

                    SMSRecord record = new SMSRecord();
                    record.setUserName(name);
                    record.setProcessInstanceId(wi.getProcessInstanceId());

                    //JpaPersistenceContextManager jpaContextManager = (JpaPersistenceContextManager) kSession.getEnvironment().get(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER);
                    //EntityManager em = jpaContextManager.getApplicationScopedEntityManager();

                    //em.merge(record);

                    // see that the ID of the user was set by Hibernate
                    System.out.println("SMS Created record: " + record);

                    // Send SMS to customer
                    //
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////7

                    try {
                        String message = DEFAULT_SMS_MESSAGE;
                        if (wi.getParameter("message") != null && wi.getParameter("message").toString().trim().length() > 1) {
                            message = wi.getParameter("message").toString().trim();
                        }
                        message = message.replaceAll("#PID#", String.valueOf(wi.getProcessInstanceId()));
                        //System.out.println("Message: " + message);

                        if (phone != null && !phone.trim().equals("")) {

                            TwilioClient client = new TwilioClient();
                            if (client.sendSMS(phone, message)) {
                                System.out.println("*** Sent SMS to " + phone + " with message " + message);
                            } else {
                                System.out.println("Failed to send SMS to phone: " + phone);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error sending SMS to phone: " + phone);
                    }
                }
            }
        } catch (Exception e) {
            /// If failed let the process continue
            e.printStackTrace();
        } finally {
            wim.completeWorkItem(wi.getId(), null);
        }
    }

    
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

    }

    protected String getValueFromObject(Object object, String fieldName) {

        // get class
        Class clazz = object != null ? object.getClass() : null;
        if (clazz == null) {
            return null;
        }

        // get object value using reflection
        String getterName = "get" + fieldName;
        try {
            @SuppressWarnings("unchecked")
            Method method = clazz.getMethod(getterName);
            Object valueObject = method.invoke(object, (Object[]) null);
            return valueObject != null ? valueObject.toString() : "";
        } catch (Exception e) {
            // ignore all reflection errors
        }

        return null;
    }
}
