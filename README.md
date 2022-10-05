# Kafka streams demo

Example to illustrate the kafka transformation using streams.

There are three projects for each use case:

- [Producer](producer/README.md): generates decimal values and sends them into a topic. (supplier)
- [Processor](processor/README.md): reads the numbers, transforms them and sends into another topic. (kafka stream)
- [Consumer](consumer/README.md): receives the numbers and generates a summary (consumer and kafka table) 

## How to run

You must provide the kafka server and change the settings on the properties for all projects, or just use the docker compose provided on the source:

    cd ./docker
    docker-compose up --build

The order to run are the same as above, because te first time the topics will be created automatically on first access.
