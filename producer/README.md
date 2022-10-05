# Producer

Application that generate a number between 1 and 10 every x seconds and sends it to a kafka topic.

Configuration parameters can be changed on the application default yaml file:

    app:
        decimal-topic: decimals-numbers
        poller-delay: 2s
