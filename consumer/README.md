# Consumer

Application that reads the roman conversion from a kafka topic and prints it to the logs. Also, it creates a kafka table with the number os occurrences of every received roman number, and every x seconds, it generates a report file on the file system.

Configuration parameters can be changed on the application default yaml file:

    app:
        roman-topic: roman-numbers
        store-topic: summary
        summary-rate: 30000
        file-pattern: summary-{ID}.txt
        output-folder: c:/temp
