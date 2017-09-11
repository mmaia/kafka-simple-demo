# Mock Stocks

This application generates stock quotes, it was created with goal of having a stream of data that can be used
to study streaming with kafka and elasticsearch.

# Running

To run the application you'll need a local accessible kafka. I prefer to run it in docker and 
[this is the one](https://github.com/mmaia/docker-compose-images/tree/master/kafka) I've used during development.


> Set your hosts to resolve the name of the kafka containers to your localhost if  you're running this application from source code, this 
is required because of how kafka resolves it's producer address internally on the docker network and your localhost network.
If you're running this application including the mockstocks application inside a docker container then kafka should already
resolve correctly.

Start all containers in background:
```bash
    docker-compose up -d
```

Attach terminal to docker logs and follow(tail):
```bash
    docker-compose logs -f
```

To scale kafka to multiple instances type:
```bash
    docker-compose scale kafka=3
```

# Tests and sonarqube

This project has integration with jacoco to generate code coverage and quality reports. Once you run `mvn test` surefire
reports will be generated in the target folder. In order to see this reports there's a sonarqube default docker-compose
available under src/main/docker that can be used to see the results. 

1. Start the docker container with sonarqube, from docker folder `docker-compsoe up -d`
2. Export results to sonarqube: `mvn sonar:sonar`
3. Open localhost:9000 and you should see sonarqube dashboard that you can navigate
