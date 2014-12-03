#!/bin/sh 
DEMO="Red Hat JBoss Keynote 2014 Demo"
AUTHORS="Burr Sutter, Eric D. Schabell, Christina Lin"
PROJECT="git@github.com:jbossdemocentral/jboss-keynote-2014.git"
PRODUCT="JBoss Keynote 2014 Demo"
JBOSS_HOME=./target/jboss-eap-6.1
FUSE_HOME=./target/jboss-fuse-6.1.0.redhat-379
SERVER_DIR=$JBOSS_HOME/standalone/deployments
SERVER_CONF=$JBOSS_HOME/standalone/configuration
SERVER_BIN=$JBOSS_HOME/bin
SUPPORT_DIR=./support
SUPPORT_LIBS=$SUPPORT_DIR/libs
SRC_DIR=./installs
PRJ_DIR=./projects
EAP=jboss-eap-6.1.1.zip
FUSE=jboss-fuse-full-6.1.0.redhat-379.zip
BPMS=jboss-bpms-6.0.2.GA-redhat-5-deployable-eap6.x.zip
VERSION=2014

# wipe screen.
clear 

echo
echo "######################################################################################################"
echo "##                                                                                                  ##"   
echo "##  Setting up the ${DEMO}                                                  ##"
echo "##                                                                                                  ##"   
echo "##                                                                                                  ##"   
echo "##   #  # ##### #   # #   # ##### ##### #####   ####  ##### #   # #####   ##### #####   #   #   #   ##"
echo "##   # #  #      # #  ##  # #   #   #   #       #   # #     ## ## #   #       # #   #  ##   #   #   ##"
echo "##   ##   ###     #   # # # #   #   #   ###     #   # ###   # # # #   #   ##### #   #   #   #####   ##"
echo "##   # #  #       #   #  ## #   #   #   #       #   # #     #   # #   #   #     #   #   #       #   ##"
echo "##   #  # #####   #   #   # #####   #   #####   ####  ##### #   # #####   ##### ##### #####     #   ##"
echo "##                                                                                                  ##"   
echo "##                                                                                                  ##"   
echo "##  brought to you by,                                                                              ##"   
echo "##             ${AUTHORS}                                         ##"
echo "##                                                                                                  ##"   
echo "##  ${PROJECT}                                          ##"
echo "##                                                                                                  ##"   
echo "######################################################################################################"
echo

command -v mvn -q >/dev/null 2>&1 || { echo >&2 "Maven is required but not installed yet... aborting."; exit 1; }

# add executeable in installs
chmod +x installs/*.zip

# make some checks first before proceeding.	
if [ -r $SRC_DIR/$EAP ] || [-L $SRC_DIR/$EAP ]; then
		echo EAP sources are present...
		echo
else
		echo Need to download $EAP package from the Customer Support Portal 
		echo and place it in the $SRC_DIR directory to proceed...
		echo
		exit
fi

if [ -r $SRC_DIR/$FUSE ] || [ -L $SRC_DIR/$FUSE ]; then
		echo Fuse sources are present...
		echo
else
		echo Need to download $FUSE package from the Customer Support Portal 
		echo and place it in the $SRC_DIR directory to proceed...
		echo
		exit
fi

# Create the target directory if it does not already exist.
if [ ! -x target ]; then
		echo "  - creating the target directory..."
		echo
		mkdir target
else
		echo "  - detected target directory, moving on..."
		echo
fi

# Move the old JBoss instance, if it exists, to the OLD position.
if [ -x $JBOSS_HOME ]; then
		echo "  - existing JBoss EAP detected..."
		echo
		echo "  - moving existing JBoss EAP aside..."
		echo
		rm -rf $JBOSS_HOME.OLD
		mv $JBOSS_HOME $JBOSS_HOME.OLD

		# Unzip the JBoss EAP instance.
		echo Unpacking JBoss EAP...
		echo
		unzip -q -d target $SRC_DIR/$EAP
else
		# Unzip the JBoss EAP instance.
		echo Unpacking new JBoss EAP...
		echo
		unzip -q -d target $SRC_DIR/$EAP
fi

# Move the old Fuse instance, if it exists, to the OLD position.
if [ -x $FUSE_HOME ]; then
		echo "  - existing JBoss FUSE detected..."
		echo
		echo "  - moving existing JBoss FUSE aside..."
		echo
		rm -rf $FUSE_HOME.OLD
		mv $FUSE_HOME $FUSE_HOME.OLD

		# Unzip the JBoss FUSE instance.
		echo Unpacking JBoss FUSE...
		echo
		unzip -q -d target $SRC_DIR/$FUSE
else
		# Unzip the JBoss FUSE instance.
		echo Unpacking JBoss FUSE...
		echo
		unzip -q -d target $SRC_DIR/$FUSE
fi

echo "  - enabling accounts logins in JBoss Fuse users.properties file..."
echo
cp $SUPPORT_DIR/users.properties $FUSE_HOME/etc/


# Unzip the required files from JBoss BPM Suite product deployable.
echo Unpacking JBoss BPM Suite...
echo
unzip -q -o -d target $SRC_DIR/$BPMS

echo "  - enabling demo accounts logins in application-users.properties file..."
echo
cp $SUPPORT_DIR/application-users.properties $SERVER_CONF

echo "  - enabling demo accounts role setup in application-roles.properties file..."
echo
cp $SUPPORT_DIR/application-roles.properties $SERVER_CONF

echo "  - enabling management accounts login setup in mgmt-users.properties file..."
echo
cp $SUPPORT_DIR/mgmt-users.properties $SERVER_CONF

echo "  - setting up standalone.xml configuration adjustments..."
echo
cp $SUPPORT_DIR/standalone.xml $SERVER_CONF

echo "  - setting up keynote BPM demo project..."
echo
cp -r $SUPPORT_DIR/bpm-suite-keynote-niogit $SERVER_BIN/.niogit

# Add execute permissions to the standalone.sh script.
echo "  - making sure standalone.sh for server is executable..."
echo
chmod u+x $JBOSS_HOME/bin/standalone.sh

echo Building the JBoss Keynote 2014 Demo projects...
echo
cd $PRJ_DIR
mvn clean install -DskipTests
cd ..

echo Setup H2 Database
echo
cd $PRJ_DIR/DBSetup
mvn process-test-resources
cd ../..


echo
echo Adding built dependencies to business central...
echo 
cp -r $SUPPORT_DIR/WEB-INF $SERVER_DIR/business-central.war
cp $PRJ_DIR/CustomerFollowUpWIH/target/*.jar $SERVER_DIR/business-central.war/WEB-INF/lib
cp $PRJ_DIR/json/target/json.jar $SERVER_DIR/business-central.war/WEB-INF/lib
cp $PRJ_DIR/twilio/target/twilio.jar $SERVER_DIR/business-central.war/WEB-INF/lib

echo
echo Adding dashbuilder setup to business central...
echo
cp -r $SUPPORT_DIR/dashbuilder $SERVER_DIR/dashbuilder.war
cp $SUPPORT_DIR/showcaseWorkspace.xml $SERVER_DIR/dashbuilder.war/WEB-INF/etc/appdata/initialData
cp $SUPPORT_DIR/showcaseKPIs.xml $SERVER_DIR/dashbuilder.war/WEB-INF/etc/appdata/initialData

# install maven build from BPM project, prevents having to login for build & deploy.
mvn install:install-file -Dfile=$SUPPORT_LIBS/customer-follow-up-1.0.jar -DgroupId=org.jboss.demo -DartifactId=customer-follow-up -Dversion=1.0 -Dpackaging=jar

echo
echo "The last steps to start the project:"
echo
echo "1. Start the BPM Suite product in the background with $SERVER_BIN/standalone.sh"

echo
echo "2. Add the following auth setitngs to your .m2/settings.xml before starting Fuse product:"
echo
echo "   <server>"
echo "      <id>fabric8.upload.repo</id>"
echo "      <username>admin</username>"
echo "      <password>admin</password>"
echo "   </server>"

echo
echo "3. Start the Fuse product with $FUSE_HOME/bin/fuse"
echo
echo "The $PRODUCT $DEMO $VERSION Setup Complete."
echo
