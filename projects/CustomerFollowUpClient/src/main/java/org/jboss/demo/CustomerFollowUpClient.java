package org.jboss.demo;

import org.jboss.demo.customersatisfaction.Customer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.services.client.api.RemoteRestRuntimeFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class CustomerFollowUpClient {

    private Properties configurationProperties = null;

    public CustomerFollowUpClient() {
    }

    public CustomerFollowUpClient(Properties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    private String getProperty(String propertyKey) {
        try {
            if (configurationProperties == null) {
                configurationProperties = new Properties();
                configurationProperties.load(
                        getClass().getClassLoader().getResourceAsStream("process-client.properties"));
            }

            String value = configurationProperties.getProperty(propertyKey);
            if (value == null) {
                throw new RuntimeException("Property " + propertyKey + " not defined");
            }

            return value;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("process-client.properties file not found in classpath");
        }
    }

    private RuntimeEngine getRuntimeEngine(String applicationContext, String deploymentId, String userId, String password) {
        try {
            URL jbpmURL = new URL(applicationContext);
            RemoteRestRuntimeFactory remoteRestSessionFactory = new RemoteRestRuntimeFactory(deploymentId, jbpmURL, userId, password);
            RuntimeEngine runtimeEngine = remoteRestSessionFactory.newRuntimeEngine();
            return runtimeEngine;
        } catch (MalformedURLException e) {
            throw new IllegalStateException("This URL is always expected to be valid! " + applicationContext, e);
        }
    }

    private RuntimeEngine getRuntimeEngine() {
        return getRuntimeEngine(
                getProperty("application.context"),
                getProperty("deployment.id"),
                getProperty("user.id"),
                getProperty("user.password"));
    }

    public ProcessInstance launchCustomerFollowupProcess(Customer customer, int daysOffsetDueDate) {
        RuntimeEngine runtimeEngine = getRuntimeEngine();

        KieSession kieSession = runtimeEngine.getKieSession();
        Map<String, Object> processVariables = new HashMap<String, Object>();
        processVariables.put("customer", customer);
        processVariables.put("initialDueDate", "P"+daysOffsetDueDate + "d");


        return kieSession.startProcess(getProperty("process.id"), processVariables);
    }

    public void sendSignal( long processId, String signal) {
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession kieSession = runtimeEngine.getKieSession();
        kieSession.signalEvent(signal, null, processId);
    }

    public Task executeNextTask(long processId, Map paramsTask) {
        RuntimeEngine runtimeEngine = getRuntimeEngine();

        TaskService taskService = runtimeEngine.getTaskService();

        List<Long> taskIds = taskService.getTasksByProcessInstanceId(processId);

        if( taskIds != null && !taskIds.isEmpty()) {
            for (Long taskId : taskIds) {
                Task task = taskService.getTaskById(taskId);
                if( task.getTaskData().getStatus().equals(Status.Ready) ||
                    task.getTaskData().getStatus().equals(Status.Reserved)    ) {
                    taskService.start(taskId,  getProperty("user.id"));
                    taskService.complete(taskId, getProperty("user.id"), paramsTask);

                    System.out.println("**** Task executed " + taskId);
                    return task;
                }
            }
        }

        return null;
    }

    public TaskSummary executeInitialTask(String taskName, String user, Map paramsTask) {
        RuntimeEngine runtimeEngine = getRuntimeEngine();

        TaskService taskService = runtimeEngine.getTaskService();

        List<TaskSummary> tasks= taskService.getTasksAssignedAsPotentialOwner(user, "en-UK");

        if( !tasks.isEmpty()) {
            for (TaskSummary taskSummary : tasks) {
                long taskId = taskSummary.getId();
                Task task = taskService.getTaskById(taskSummary.getId());

                if( taskSummary.getName().equalsIgnoreCase(taskName) &&
                   (task.getTaskData().getStatus().equals(Status.Ready) ||
                    task.getTaskData().getStatus().equals(Status.Reserved))) {
                    taskService.start(taskId,  getProperty("user.id"));
                    taskService.complete(taskId, getProperty("user.id"), paramsTask);

                    System.out.println("**** Task executed " + taskId);
                    taskSummary.getProcessInstanceId();
                    return taskSummary;
                }
            }
        }

        return null;
    }
}
