cas-sso
=======

Hoteia CAS SSO project for : Qalingo project, Rabble DM project

To run webapp-sso-cas project, navigate to webapp-sso-cas folder and execute these two commands:
mvn org.apache.activemq.tooling:activemq-maven-plugin:5.9.0:run
mvn tomcat7:run-war

Alternatively, use your IDE to run respectively the two Maven builds with the 
org.apache.activemq.tooling:activemq-maven-plugin:5.9.0:run 
tomcat7:run-war goals

The CAS application will be available at :
https://localhost:8443/cas/

To run test webapps, navigate to webapp-sso-redirect-test or webapp-sso-standalone-test and execute commands:
mvn tomcat7:run

Alternatively, use your IDE to run Maven build with the tomcat7:run goal.

The application will be available at:
https://localhost:9443/webapp-sso-redirect-test
https://localhost:9444/webapp-sso-standalone-test

To verify that CAS is working, try accessing /secure on the test webapp. You will be redirected to CAS for login.
For username: casuser
For the password: Mellon

After a successful login, you will be redirected to the start page.
