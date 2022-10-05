# Processor

Application that reads a number from a kafka topic, converts it to an object with the original value and the conversion to roman number, and sends it to another kafka topic.

Configuration parameters can be changed on the application default yaml file:

    app:
        decimal-topic: decimals-numbers
        roman-topic: roman-numbers
