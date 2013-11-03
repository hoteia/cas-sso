cas-sso
=======

Hoteia CAS SSO project for : Qalingo project, Rabble DM project

To run cas-extension project, navigate to cas-extension folder and execute these two commands: mvn activemq:run and mvn tomcat7:run-war
(alternatively, use your IDE to run two Maven builds with the activemq:run and tomcat7:run-war goals, respectively).

To run spring-mvc-cas-poc, navigate to spring-mvc-cas-poc and execute mvn tomcat7:run command
(alternatively, use your IDE to run Maven build with the tomcat7:run goal). The application will be available at https://localhost:9443/spring-mvc-cas-poc

To run spring-mvc-cas-poc-extra, navigate to spring-mvc-cas-poc-extra and execute mvn tomcat7:run command
(alternatively, use your IDE to run Maven build with the tomcat7:run goal). The application will be available at https://localhost:9443/spring-mvc-cas-poc-extra

To verify that CAS is working, try accessing https://localhost:9443/spring-mvc-cas-poc/secure. You will be redirected to CAS for login;
use casuser for username and Mellon for password. After a successful login, you will be redirected to the start page.
Now try accessing https://localhost:9443/spring-mvc-cas-poc-extra/secure and verify that you are now logged in as casuser.
