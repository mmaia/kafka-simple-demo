# Mock Stocks

This application generates stock quotes, it was created with goal of having a stream of data that can be used
to study mainly kafka and elasticsearch.

# Running

To run the application you'll need a local accessible kafka. I prefer to run it in docker and 
[this is the one](https://github.com/mmaia/docker-compose-images/tree/master/kafka) I use during development.


# Tests and sonarqube

This project has integration with jacoco to generate code coverage and quality reports. Once you run `mvn test` surefire
reports will be generated in the target folder. In order to see this reports there's a sonarqube default docker-compose
available under src/main/docker that can be used to see the results. 

1. Start the docker container with sonarqube, from docker folder `docker-compsoe up -d`
2. Export results to sonarqube: `mvn sonar:sonar`
3. Open localhost:9000 and you should see sonarqube dashboard that you can navigate
