# UserProxy

This application is a proxy to (user service) that count number of requests for each user
(and add "calculation" field).

## Data Base
Application use H2 data base. <br />l
Link to h2-console: http://localhost:8080/h2-console

## How to run
- run `mvn clean`
- run `mvn package`
- run `java -jar target/usersProxy-0.0.1-SNAPSHOT.jar`