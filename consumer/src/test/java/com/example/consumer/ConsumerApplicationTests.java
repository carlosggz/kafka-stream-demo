package com.example.consumer;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.shared.dtos.NumberInfo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

@SpringBootTest(properties = {
    "app.roman-topic=romans",
    "app.store-topic=summary",
    "app.summary-rate=7000",
    "app.file-pattern=summary-{ID}.txt",
    "app.output-folder=build/test-files",
    "app.boostrap-servers=localhost:9092"
})
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"},
    topics = {"romans", "summary"}
)
class ConsumerApplicationTests {

    private static final String ROMANS_TOPIC = "romans";
    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    Deserializer<NumberInfo> numberInfoDeserializer;

    @Autowired
    Serializer<NumberInfo> numberInfoSerializer;

    @Value("${app.file-pattern}")
    String filePattern;

    @Value("${app.output-folder}")
    String outputFolder;

    private Producer<String, NumberInfo> producer;
    private Consumer<String, NumberInfo> consumer;

    @SneakyThrows
    @BeforeEach
    void setup() {
        producer = configureProducer();
        consumer = configureConsumer();

        var folder = Path.of(outputFolder);

        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        } else {
            cleanUpFilesFolder();
        }
    }

    @SneakyThrows
    @AfterEach
    void cleanUp() {
        producer.close();
        consumer.close();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void summaryFileIsGeneratedAfterRate() {

        //given
        var numbers = List.of(
            new NumberInfo(10, "X"),
            new NumberInfo(11, "XI"),
            new NumberInfo(10, "X")
        );

        var patternX = Pattern.compile("^X: [1-9][0-9]*$", Pattern.MULTILINE);
        var patternXI = Pattern.compile("^XI: [1-9][0-9]*$", Pattern.MULTILINE);
        var patternFile = Pattern.compile(
            "^summary-\\d{4}-\\d{2}-\\d{2}-\\d{2}-\\d{2}-\\d{2}[.]txt$", Pattern.MULTILINE);

        //when
        numbers.forEach(x -> producer.send(new ProducerRecord<>(ROMANS_TOPIC, null, x)));
        producer.flush();

        //then
        await()
            .atMost(Duration.ofSeconds(10))
            .untilAsserted(() -> {

                var allFiles = new ArrayList<Path>();

                try (var stream = Files.newDirectoryStream(Path.of(outputFolder))) {
                    stream.forEach(x -> allFiles.add(x.getFileName()));
                }

                assertEquals(1, allFiles.size());
                var fileName = allFiles.get(0).toString();
                assertNotNull(fileName);
                assertTrue(patternFile.matcher(fileName).find());

                var fileContent = Files
                    .readString(Path.of(outputFolder, fileName))
                    .replace("\r", "")
                    .split("\n");

                assertEquals(2, fileContent.length);

                assertTrue(patternX.matcher(fileContent[0]).find());
                assertTrue(patternXI.matcher(fileContent[1]).find());
            });
    }

    private Consumer<String, NumberInfo> configureConsumer() {
        val consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        val consumer = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(),
            numberInfoDeserializer)
            .createConsumer();
        consumer.subscribe(Collections.singleton(ROMANS_TOPIC));
        return consumer;
    }

    private Producer<String, NumberInfo> configureProducer() {
        val producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        return new DefaultKafkaProducerFactory<>(producerProps, new StringSerializer(), numberInfoSerializer)
            .createProducer();
    }

    private void cleanUpFilesFolder() throws IOException {
        try (var stream = Files.newDirectoryStream(Paths.get(outputFolder))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    Files.delete(path);
                }
            }
        }
    }
}
