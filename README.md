JBoss Middleware Keynote Demo 2014
==================================

This is an automated setup for running the JBoss Middleware Keynote demo from Summit 2014 in San Fransico. It was leveraging the
iPaaS infrastructure as provided on Openshift and will also setup to be run locally on your own JBoss Fuse and JBoss BPM Suite installation.

It will obtain all tweets using hashtags #demoup or #demodown, route to A-MQ queue, where it will pull from A-MQ, query salesforce and possibly create a new process.


Setup and configuration
-----------------------
See Quick Start Guide in project as ODT and PDF for details on installation. For those that can't wait:

1. clone this project.

2. see README in 'installs' directory and add required products.

3. run 'init.sh'. 


4. add fabric server passwords for Maven Plugin to your `~/.m2/settings.xml` file the fabric server's user and password so that the maven plugin can login to the fabric.

    <server>
      <id>fabric8.upload.repo</id>
      <username>admin</username>
      <password>admin</password>
    </server>

5. Find jboss.keynote2014.twitter.properties under projects/twitter/stream/src/main/fabric8 and change the twitter developer details to your own credentials you can setup on https://dev.twitter.com by adding a keynote app in your account at https://apps.twitter.com:
	  
	  `consumer.key = w0LGk3eptny8oGEKm2oBxheE4`
	  `consumer.secret = zg7w1NpqmObJsjAuyyzM9OXccjx4q2xQe3YFeicT0Lc5JPn3RR`
	  `access.token = 144079408-5iFeWba0UatSMQOFukpUYvMlAwF1Sc0VPgFrxE9p`
	  `access.token-secret = M3GMh7AfwdXItdVbEHu88UHAKOSiV82DRBBWtaKiOPPGJ`   

6. start JBoss BPM Suite & JBoss Fuse product as instructed.

7. start up fabric in fuse console: 

     `fabric:create --wait-for-provisioning`

8. Under projects/twitter/stream run maven command:

     `mvn fabric8:deploy`

9. Under projects/salesforce run maven command:

     `mvn fabric8:deploy`

10. Ensure standalone MQ broker exits, view in the MQ tab on the runtime page, see example image below. 

11. Create Twitter and add  keynote-twitter-stream profile containers
	 ![Twitter Container](https://github.com/eschabell/jboss-keynote-demo-2014/blob/christina/docs/demo-images/twitterCon.png?raw=true)
	 Create Salesforce and add keynote-salesforce profile containers in Fuse using above created profiles
	 ![Salesforce Container](https://github.com/eschabell/jboss-keynote-demo-2014/blob/christina/docs/demo-images/salesforceCon.png?raw=true)

11. Tweet message with hashtags #demoup or #demodown, should trigger twitter route and salesforce routes, see example images below.


Issues
------
- Cannot really send SMS. Don't have a correct account. If you setup this paid serivce, can setup salesforce account witth your
	phone number, edit jboss.keynote2014.salesforce.properties under projects/salesforce/src/main/fabric8 and change the salesforce return details
	  
	  `salesforce.email = clin@redhat.com`
	  
		`salesforce.mobilePhone = +886983186479`
		
		`salesforce.firstName = Christina`
		
		`salesforce.lastName = Lin`
		
		`salesforce.dept = mkt`

- Need to update BAM datasource. 



Supporting articles
-------------------
[Original (1h) video of JBoss Keynote Demo 2014 in San Francisco](http://youtu.be/XPK2RTqlBxk)


Released versions
-----------------
- v0.2 JBoss Fuse 6.1.0, JBoss BPM Suite 6.0.2, with keynote demo installed.

- v0.1 JBoss Fuse 6.1.0, Keynote demo installed.


![Customer Process](https://raw.githubusercontent.com/eschabell/jboss-keynote-demo-2014/christina/docs/demo-images/customer-process.png?raw=true)
![MQ Broker](https://github.com/eschabell/jboss-keynote-demo-2014/blob/master/docs/demo-images/fuse-runtime-broker.png?raw=true)
![Twitter Stream Camel Route](https://github.com/eschabell/jboss-keynote-demo-2014/blob/christina/docs/demo-images/fuse-twitterstream-camel-route.png?raw=true)
![Saleforce Camel Route](https://github.com/eschabell/jboss-keynote-demo-2014/blob/christina/docs/demo-images/fuse-salesforce-camel-route.png?raw=true)
![Mock Saleforce Camel Route](https://github.com/eschabell/jboss-keynote-demo-2014/blob/christina/docs/demo-images/mocksalesforce-camel-route.png?raw=true)


