package com.example.consumer.components;

import static org.apache.kafka.streams.StoreQueryParameters.fromNameAndType;
import static org.apache.kafka.streams.state.QueryableStoreTypes.keyValueStore;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TableConsumerComponent {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;
    private final String numbersStore;
    private final String filePattern;
    private final String outputFolder;

    public TableConsumerComponent(
        StreamsBuilderFactoryBean streamsBuilderFactoryBean,
        @Value("${app.store-topic}") final String numbersStore,
        @Value("${app.output-folder}") final String outputFolder,
        @Value("${app.file-pattern}") final String filePattern) {
        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
        this.numbersStore = numbersStore;
        this.filePattern = filePattern;
        this.outputFolder = outputFolder;
    }


    @Scheduled(fixedRateString = "${app.summary-rate}")
    public void displaySummary() {
        Optional
            .of(streamsBuilderFactoryBean)
            .map(StreamsBuilderFactoryBean::getKafkaStreams)
            .map(x -> x.store(fromNameAndType(numbersStore, keyValueStore())))
            .map(ReadOnlyKeyValueStore::all)
            .ifPresent(this::writeToFile);
    }

    private void writeToFile(org.apache.kafka.streams.state.KeyValueIterator<?, ?> iterator){
        var fileName = getNewFileName();
        log.info("Writing summary to file {}", fileName);
        try (iterator; var file = new FileOutputStream(fileName)) {
            while (iterator.hasNext()) {
                var current = iterator.next();
                var toWrite = String.format("%s: %s%n", current.key, current.value).getBytes(StandardCharsets.UTF_8);
                file.write(toWrite);
            }
        } catch (Exception ex) {
            log.error("Error processing summary ", ex);
        }
    }
    private String getNewFileName() {
        return Path.of(
            outputFolder,
            filePattern.replace("{ID}", LocalDateTime.now().format(DATE_TIME_FORMATTER)))
            .toString();
    }
}
