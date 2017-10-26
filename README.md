#kafka-simple-demo

This project is used to create a continuous stream of mock data that simulates, in a very simple way, quote prices from
the stock market, the main goal here is not focus on accurace regarding the domain of the securities market that is very 
complex, it's on kafka, please check [this post](http://codespair.com/preview/zAzFd0PecDWdDMQ9X7/) for further details.

Implemented using:

 - Kafka 0.10.2
 - Spring kafka producer
 - Spring boot
 - Java 8
 - Docker CE
 - Docker compose

# Running

To run the application you'll need a local accessible kafka. I prefer to run it with docker-compose(currently docker-compose version 1.16.1, build 6d1ac21 ) and [this is the one](https://github.com/wurstmeister/kafka-docker) I've used during development.

> Set your hosts to resolve the name of the kafka containers to your localhost if you're running this application from source code, this is required because of how kafka resolves it's producer address internally on the docker network and your localhost network. If you're running this application including the mockstocks application inside a docker container then kafka should already resolve correctly.

Start all containers in background:

```bash
    docker-compose up -d
```

Attach terminal to docker logs and follow(tail):

```bash
    docker-compose logs -f
```

Once it's up you can check the container list with `docker ps`

# Tests and sonarqube

This project has integration with jacoco to generate code coverage and quality 
reports. 
Once you run `mvn test` surefire reports will be generated in the target folder. 
In order to see this reports there's a sonarqube default docker-compose 
available under src/main/docker that can be used to see the results.

1. Start the docker container with sonarqube, from docker folder 
    `docker-compsoe up -d`
2. Export results to sonarqube: `mvn sonar:sonar`
3. Open localhost:9000 and you should see sonarqube dashboard that you can navigate
 