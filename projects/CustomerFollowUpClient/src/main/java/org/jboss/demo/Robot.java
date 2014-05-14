package org.jboss.demo;

import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;

import java.util.HashMap;
import java.util.Random;

public class Robot {
    private String INITIAL_TASK = "Tweet review";
    private long WAIT_TIME = 5000L;             /// Type task
    private float probabilityEscalation = 0.3F; /// Probability of escalation

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: <USER> <WAIT-TIME>");
            return;
        }

        Robot robot = new Robot();
        robot.run(args[0], Long.parseLong(args[1]));
    }

    public void run(String user, long waitTime) {
        this.WAIT_TIME = waitTime;
        System.out.println("Robot started: User: " + user);

        Random random = new Random();
        if (random.nextFloat() <= probabilityEscalation) {
            runEscalationPath(user);
        } else {
            runHappyPath(user);
        }
    }


    public void runHappyPath(String user) {

        System.out.println(".... Running through standard path ....");

        CustomerFollowUpClient client = new CustomerFollowUpClient();
        HashMap params = new HashMap();

        // Go to task 'Call Customer'
        //params.put("customer_out", customer);
        params.put("requires_escalation_out", false);
        //params.put("customer_in", customer);

        TaskSummary taskSummary = client.executeInitialTask(INITIAL_TASK, user, params);
        if (taskSummary == null) {
            System.out.println("... No tasks found");
            return;
        }
        simulateWaitTime();

        long processInstanceId = taskSummary.getProcessInstanceId();

        // Go to task 'Update sales force account'
        Task task = client.executeNextTask(processInstanceId, params);
        simulateWaitTime();

        if (task != null) {

            // Verify no pending tasks
            task = client.executeNextTask(processInstanceId, params);
        }

        System.out.println(".... Happy path finished ....");
    }

    public void runEscalationPath(String user) {
        System.out.println(".... Running through escalation path ....");

        CustomerFollowUpClient client = new CustomerFollowUpClient();
        HashMap params = new HashMap();
        Task task = null;

        // Go to task 'Review customer case'
        //params.put("customer_out", customer);
        params.put("requires_escalation_out", true);

        TaskSummary taskSummary = client.executeInitialTask(INITIAL_TASK, user, params);

        if (taskSummary == null) {
            System.out.println("... No tasks found");
            return;
        }

        long processInstanceId = taskSummary.getProcessInstanceId();

        simulateWaitTime();

        // Go to task 'Provide feedback'
        params.put("action_out", "feedback");
        task = client.executeNextTask(processInstanceId, params);
        if (task == null) {
            return;
        }

        simulateWaitTime();

        // Go to task 'Review customer case'
        task = client.executeNextTask(processInstanceId, params);
        if (task == null) {
            return;
        }

        simulateWaitTime();

        // Go to task 'Call customer'
        params.put("action_out", "call");
        task = client.executeNextTask(processInstanceId, params);
        if (task == null) {
            return;
        }

        simulateWaitTime();

        // Go to task 'Update sales force account'
        task = client.executeNextTask(processInstanceId, params);
        if (task == null) {
            return;
        }

        simulateWaitTime();

        // Verify no pending tasks
        task = client.executeNextTask(processInstanceId, params);
        if (task == null) {
            return;
        }

        System.out.println(".... Escalation path finished .....");
    }

    private void simulateWaitTime() {
        try {
            System.out.println(" ... Waiting ....");
            Thread.sleep(WAIT_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
