cas-sso
=======

Hoteia CAS SSO project for : Qalingo project, Rabble DM project

To run webapp-sso-cas project, navigate to webapp-sso-cas folder and execute these two commands: mvn org.apache.activemq.tooling:activemq-maven-plugin:5.9.0:run and mvn tomcat7:run-war
(alternatively, use your IDE to run two Maven builds with the org.apache.activemq.tooling:activemq-maven-plugin:5.9.0:run and tomcat7:run-war goals, respectively).

To run webapp-sso-redirect-test, navigate to webapp-sso-redirect-test and execute mvn tomcat7:run command
(alternatively, use your IDE to run Maven build with the tomcat7:run goal). The application will be available at https://localhost:9443/webapp-sso-redirect-test

To verify that CAS is working, try accessing https://localhost:9443/webapp-sso-redirect-test/secure. You will be redirected to CAS for login;
use casuser for username and Mellon for password. After a successful login, you will be redirected to the start page.
