package org.jboss.demo;


import org.jboss.demo.customersatisfaction.Customer;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class SimulateLoadTest {
    private final static String[] TAGS = {"demoup", "demodown"};
    private final static String[] NAMES = {"Roxie Foraker", "Jamie Gilbeau", "Nita Marling", "Darryl Innes", "Julio Burdge", "Neva Hunger", "Kathrine Janas", "Jerri Preble"};
    private final static String[] MESSAGES = {
            "The future belongs to those who believe in the beauty of their dreams.",
            "Dream as if you'll live forever. Live as if you'll die today.",
            "Sleep is the best meditation.",
            "A wise man can learn more from a foolish question than a fool can learn from a wise answer.",
            "If you're not making mistakes, then you're not doing anything. I'm positive that a doer makes mistakes.",
            "In order to carry a positive action we must develop here a positive vision.",
            "A successful man is one who makes more money than his wife can spend. A successful woman is one who can find such a man.",
            "Do not take life too seriously. You will never get out of it alive.",
            "A day without sunshine is like, you know, night.",
            "People who think they know everything are a great annoyance to those of us who do.",
            "Behind every great man is a woman rolling her eyes.",
            "If the facts don't fit the theory, change the facts."
    };

    @Test
    public void testLoadData() {
        simulate(30, 200, 2F, false);

    }

    @Test
    public void testRealTime() {
        simulate(30, 200, 2F, true);

    }

    private void simulate(int durationInMinutes,
                          int numberOfTweeters,
                          float averageTweetsPerPerson,
                          boolean realTime
    ) {
        long seconds = durationInMinutes * 60L;


        final EntityManager entityManager = Persistence.createEntityManagerFactory("TEST").createEntityManager();

        int nTweets = 0;

        final Date startTime = new Date();
        final Random random = new Random(System.currentTimeMillis());

        try {
            long baseId = System.currentTimeMillis();
            for (int i = 0; i < seconds; i++) {
                long beginTime = System.currentTimeMillis();
                if (realTime) {
                    System.out.println(" ::: " + (i / 60) + "m " + (i % 60) + " sec");
                }
                for (int p = 0; p < numberOfTweeters; p++) {

                    float pTweet = 1F / (float) seconds * averageTweetsPerPerson;

                    if (random.nextDouble() <= pTweet) {
                        final long recordId = baseId++;
                        final int customerNumber = p;
                        final int second = i;
                        new Thread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        Calendar calendar = Calendar.getInstance();
                                        Customer customer = buildCustomer(customerNumber);

                                        TweeterRecord record = new TweeterRecord();
                                        record.setUser(customer.getName());
                                        record.setUniqueName(customer.getTweeterId());
                                        record.setSalesforce(customerNumber % 2 == 0);

                                        calendar.setTime(startTime);
                                        calendar.add(Calendar.SECOND, second);
                                        record.setTweetDate(calendar.getTime());

                                        record.setMessage(getRandomMessage());
                                        record.setTag(getRandomTag());

                                        record.setId(recordId);

                                        entityManager.getTransaction().begin();

                                        entityManager.persist(record);
                                        entityManager.getTransaction().commit();

                                        //entityManager.getTransaction().commit();

                                        System.out.println("Tweet by customer: " + record);

                                        // Launch data
                                        CustomerFollowUpClient client = new CustomerFollowUpClient();

                                        int nDaysOffset = new Random().nextInt(10);

                                        System.out.println("Launching process :");

                                        ProcessInstance processInstance = client.launchCustomerFollowupProcess(customer, nDaysOffset);

                                        Assert.assertNotNull(processInstance);

                                        System.out.println("Process ID launched:" + processInstance.getId());

                                    }
                                }
                        ).run();
                        nTweets++;
                    }
                }

                if (realTime) {
                    long waitTime = 1000L - (System.currentTimeMillis() - beginTime);
                    if (waitTime > 0) {
                        try {
                            Thread.sleep(waitTime);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        } finally {
            entityManager.close();
        }

        System.out.println("--------------------------------");
        System.out.println("Simulated " + nTweets + " tweets");

    }

    private Customer buildCustomer(int customerNumber) {
        Customer customer = new Customer();
        String name = NAMES[customerNumber % (NAMES.length)];
        customer.setName(name);
        customer.setCustomerComments("The software rocks....");
        customer.setPhone(""); // No phone to avoid SMS
        customer.setTweeterId(getTweeterId(name) + customerNumber);
        return customer;
    }

    private String getRandomTag() {
        Random r = new Random();
        int n = r.nextInt(2);
        return TAGS[n];
    }

    private String getRandomMessage() {
        Random r = new Random();
        int n = r.nextInt(MESSAGES.length);
        return MESSAGES[n];
    }

    private String getTweeterId(String name) {
        return name.toLowerCase().replaceAll("\\s", "");
    }

}


