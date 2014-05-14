JBoss Middleware Keynote Demo 2014
==================================

This is an automated setup for running the JBoss Middleware Keynote demo from Summit 2014 in San Fransico. It was leveraging the
iPaaS infrastructure as provided on Openshift and will also setup to be run locally on your own JBoss Fuse and JBoss BPM Suite installation.

It will obtain all tweets using hashtags #demoup or #demodown, route to A-MQ queue, where it will pull from A-MQ, query salesforce and possibly create a new process.


Setup and configuration
-----------------------
See Quick Start Guide in project as ODT and PDF for details on installation. For those that can't wait:

1. clone this project

2. see README in 'installs' directory and add required products 

3. run 'init.sh' & read output

After this setup the basic run for posting Twitter message that triggers this keynote to run camel route, then salesforce route will
pick up the message to process through, add it to the BAM dashboard datasource for monitoring, then calling a BPM Process. 

There are still manual actions needed to complete the full keynote demo scenario:

4. start JBoss BPM Suite & JBoss Fuse product.

5. add fabric server passwords for Maven Plugin, see below cut&paste into .m2/settings.xml.

5. start up fabric in fuse console: 

     `fabric:create --wait-for-provisioning`

6. deploy twitter stream keynote profile, in projects/twitter/stream run maven command:

     `mvn fabric8:deploy -Dfabric8.jolokiaUrl=http://localhost:8181/jolokia`

7. deploy salesforce keynote profile, in projects/salesforce run maven command:

     `mvn fabric8:deploy -Dfabric8.jolokiaUrl=http://localhost:8181/jolokia`

8. Ensure standalone MQ broker exits, view in the MQ tab on the runtime page, see example image below. 

9. Create Twitter and Salesforce containers in Fuse using above created profiles, see Twitter image below for example.

10. Tweet message with hashtags #demoup or #demodown, should trigger twitter route and salesforce routes, see example images below.

Configuring JBoss Fuse Maven Plugin
-----------------------------------
Add to your `~/.m2/settings.xml` file the fabric server's user and password so that the maven plugin can login to the fabric.

    <server>
      <id>fabric8.upload.repo</id>
      <username>admin</username>
      <password>admin</password>
    </server>

Issues
------
- Salesforce camel route hangs at insert into database node in route, due to trying to access a mysql DB. Needs to use h2 and insert into BAM dashboard tables for monitoring.

- BPM process at end of salesforce route not yet reached.


Supporting articles
-------------------
[Original (1h) video of JBoss Keynote Demo 2014 in San Francisco](http://youtu.be/XPK2RTqlBxk)


Released versions
-----------------
- v0.1 JBoss Fuse 6.1.0, Keynote demo installed.

![Install Console](https://github.com/eschabell/jboss-keynote-demo-2014/blob/master/docs/demo-images/install-console.png?raw=true)
![MQ Broker](https://github.com/eschabell/jboss-keynote-demo-2014/blob/master/docs/demo-images/fuse-runtime-broker.png?raw=true)
![Creating Twitter Stream Container](https://github.com/eschabell/jboss-keynote-demo-2014/blob/master/docs/demo-images/fuse-create-twitterstream-container.png?raw=true)
![Twitter Stream Camel Route](https://github.com/eschabell/jboss-keynote-demo-2014/blob/master/docs/demo-images/fuse-twitterstream-camel-route.png?raw=true)
![Saleforce Camel Route](https://github.com/eschabell/jboss-keynote-demo-2014/blob/master/docs/demo-images/fuse-salesforce-camel-route.png?raw=true)



