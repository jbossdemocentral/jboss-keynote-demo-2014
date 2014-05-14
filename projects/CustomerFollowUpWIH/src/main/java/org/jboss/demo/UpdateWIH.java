package org.jboss.demo;

import org.drools.persistence.jpa.JpaPersistenceContextManager;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class UpdateWIH implements WorkItemHandler {
    private KieSession kSession;


    public UpdateWIH(KieSession kSession) {
        this.kSession = kSession;
    }


    public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
        System.out.println("**************************************************************");
        System.out.println("Executing Update Salesform workitem : ");
        System.out.println("**************************************************************");

        try {
            System.out.println("Deleting record");
            WorkflowProcessInstance processInstance = (WorkflowProcessInstance) kSession.getProcessInstance(wi.getProcessInstanceId());
            if (processInstance != null) {
                JpaPersistenceContextManager jpaContextManager = (JpaPersistenceContextManager) kSession.getEnvironment().get(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER);
                EntityManager em = jpaContextManager.getApplicationScopedEntityManager();

                Query query = em.createQuery(
                        "DELETE FROM SMSRecord r WHERE r.processInstanceId = :id");
                int deletedCount = query.setParameter("id", processInstance.getId()).executeUpdate();

                // see that the ID of the user was set by Hibernate
                System.out.println("SMS Deleted: " + deletedCount);
            }
        } catch (Exception e) {
            /// If failed let the process continue
            e.printStackTrace();
        } finally {
            wim.completeWorkItem(wi.getId(), null);
        }
    }

    public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
