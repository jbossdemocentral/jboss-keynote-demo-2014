package org.jboss.demo;


import org.jboss.demo.customersatisfaction.Customer;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;

import java.util.HashMap;
import java.util.Random;

public class CustomerFollowUpClientTest {
    private final static String INITIAL_TASK = "Tweet review";
    private final static String USER = "luis";

    @Test
    public void testLaunchProcess() {
        CustomerFollowUpClient client = new CustomerFollowUpClient();

        Customer customer = buildCustomer();

        int nDaysOffset = new Random().nextInt(10);

        ProcessInstance processInstance = client.launchCustomerFollowupProcess(customer, nDaysOffset);


        Assert.assertNotNull(processInstance);

        System.out.println("Process ID:" + processInstance.getId());

   }

    @Test
    public void testLaunchMultipleProcess() {
        for( int i = 0; i < 100; i++) {
        CustomerFollowUpClient client = new CustomerFollowUpClient();

        Customer customer = buildCustomer();

        int nDaysOffset = new Random().nextInt(10);

        ProcessInstance processInstance = client.launchCustomerFollowupProcess(customer, nDaysOffset);


        Assert.assertNotNull(processInstance);

        System.out.println("Process ID:" + processInstance.getId());
        }

   }



    @Test
    public void testHappyPath() {
        CustomerFollowUpClient client = new CustomerFollowUpClient();
        Customer customer = buildCustomer();
        HashMap params = new HashMap();

        int nDaysOffset = new Random().nextInt(10);

        // Launch a process
        ProcessInstance processInstance = client.launchCustomerFollowupProcess(customer, nDaysOffset);
        Assert.assertNotNull(processInstance);

        // Go to task 'Call Customer'
        //params.put("customer_out", customer);
        params.put("requires_escalation_out", false);
        //params.put("customer_in", customer);

        TaskSummary taskSummary = client.executeInitialTask(INITIAL_TASK, USER, params);

        Assert.assertNotNull(taskSummary);
        Assert.assertNotNull(taskSummary.getProcessInstanceId());

        long processInstanceId = taskSummary.getProcessInstanceId();

        // Go to task 'Update sales force account'
        Task task = client.executeNextTask(processInstanceId, params);
        Assert.assertNotNull(task);

        // Verify no pending tasks
        task = client.executeNextTask(processInstanceId, params);
        Assert.assertNull(task);
    }

    @Test
    public void testEscalationPath() {
        CustomerFollowUpClient client = new CustomerFollowUpClient();
        Customer customer = buildCustomer();
        HashMap params = new HashMap();
        Task task = null;

        int nDaysOffset = new Random().nextInt(10);

        // Launch process
        ProcessInstance processInstance = client.launchCustomerFollowupProcess(customer, nDaysOffset);
        Assert.assertNotNull(processInstance);

        // Go to task 'Review customer case'
        //params.put("customer_out", customer);
        params.put("requires_escalation_out", true);

        TaskSummary taskSummary = client.executeInitialTask(INITIAL_TASK, USER, params);

        Assert.assertNotNull(taskSummary);
        Assert.assertNotNull(taskSummary.getProcessInstanceId());

        long processInstanceId = taskSummary.getProcessInstanceId();

        // Go to task 'Provide feedback'
        params.put("action_out", "feedback");
        task = client.executeNextTask(processInstanceId, params);
        Assert.assertNotNull(task);

        // Go to task 'Review customer case'
        task = client.executeNextTask(processInstanceId, params);
        Assert.assertNotNull(task);

        // Go to task 'Call customer'
        params.put("action_out", "call");
        task = client.executeNextTask(processInstanceId, params);
        Assert.assertNotNull(task);

        // Go to task 'Update sales force account'
        task = client.executeNextTask(processInstanceId, params);
        Assert.assertNotNull(task);

        // Verify no pending tasks
        task = client.executeNextTask(processInstanceId, params);
        Assert.assertNull(task);
    }

    @Test
    public void testSignal() {
        CustomerFollowUpClient client = new CustomerFollowUpClient();
        Customer customer = buildCustomer();
        HashMap params = new HashMap();

        int nDaysOffset = new Random().nextInt(10);

        // Launch process
        ProcessInstance processInstance = client.launchCustomerFollowupProcess(customer, nDaysOffset);
        Assert.assertNotNull(processInstance);

        // Go to task 'Call Customer'
        //params.put("customer_out", customer);
        params.put("requires_escalation_out", false);

        Task task = client.executeNextTask(processInstance.getId(), params);

        Assert.assertNotNull(task);

        // Send signal to process
        client.sendSignal(processInstance.getId(), "customerCallReceived");

        // Verify no pending tasks
        task = client.executeNextTask(processInstance.getId(), params);
        Assert.assertNull(task);
    }


    private Customer buildCustomer() {
        Customer customer = new Customer();
        customer.setName("John Smith");
        customer.setCustomerComments("The software rocks....");
        customer.setPhone("");
        customer.setTweeterId("Tweeter");
        return customer;
    }
}
