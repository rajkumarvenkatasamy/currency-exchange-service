# currency-exchange-service
Demo app to explain how data initialization in Oracle DB can be done using Testcontainers

## Instructions to Build and Test

1) Ensure you have JAVA_HOME set as jdk 1.7. 

2) Change your current directory to currency-exchange-service project directory where pom.xml is present.

3) Run the command `mvnw.cmd install` (if windows) or use mvnw, available for your use in the same directory.

4) The build will take time as it will first download the oracle container image from the dockerhub. So, wait for its completion. Tests will get auto-invoked towards the end and the build is expected to get succeeded.