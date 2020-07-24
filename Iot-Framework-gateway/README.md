# IotGateway
This is a framework to handle gateways able to send and receive data to/from a backend solution for the Internet of Things. 

In this project, the design of an extensible software framework is presented, which allows gateway devices to communicate and store data produced by sensors and received by actuators, while providing the ability to connect with IoT platforms focused on Smart Campus. This allows the different IoT’s use cases to be implemented easier focusing on the management of final devices (sensors and actuators) while delegating the external and general functionalities to the framework.

A use case was developed in order to test the provided framework’s functions, its extensibility, portability and ability to be connected to an IoT platform.
It's important to mention that the use cases can be implemented using different programming languages, not only Java. 

The technologies, languages and protocols used to build this project are the following:

	1. Spring Boot
	2. Vertx
	3. MongoDB
	4. ActiveMQ with AMQP
	5. REST

## Getting Started
A mongodb instance at port 27017 (default) is required. See [MongoDB](https://docs.mongodb.com/manual/administration/install-community/)
First run `registry` and then `core`, then all user's custom microservices.

## Expose gateway
Use port fordwarding to expose edge framework and also custom micorservices.
To expose edge frameworks use in ssh:
`ssh -R {customDomain}:80:localhost:8080 serveo.net`
The framework is now exposed at: `https://{customDomain}.serveo.net`.
For more information go to [Serveo](https://serveo.net/).

## Installing a jar on maven locally.
mvn install:install-file -Dfile="[jar's name]" -DgroupId="[Group's id]" -DartifactId="[Artifact's id]" -Dversion="[Jar's version]" -Dpackaging="jar"
Example:
	mvn install:install-file -Dfile="starter-1.0.0-SNAPSHOT.jar" -DgroupId="io.vertx" -DartifactId="starter" -Dversion="1.0.0-SNAPSHOT" -Dpackaging="jar"
