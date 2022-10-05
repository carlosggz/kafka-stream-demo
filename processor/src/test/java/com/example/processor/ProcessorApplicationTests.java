package com.example.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.shared.dtos.NumberInfo;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import lombok.val;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

@SpringBootTest(properties = {
    "app.decimal-topic=decimals",
    "app.roman-topic=romans",
    "app.boostrap-servers=localhost:9092"
})
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"},
    topics = {"decimals", "romans"}
)
class ProcessorApplicationTests {

    private static final String DECIMALS_TOPIC = "decimals";
    private static final String ROMANS_TOPIC = "romans";

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    Deserializer<NumberInfo> numberInfoDeserializer;

    private Producer<String, String> producer;
    private Consumer<String, NumberInfo> consumer;

    @BeforeEach
    void setup() {
        producer = configureProducer();
        consumer = configureConsumer();
    }

    @AfterEach
    void cleanUp() {
        producer.close();
        consumer.close();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void whenANumberIsReceivedATransformationHappens() {

        //given
        val expectedResult = new NumberInfo(10, "X");
        ProducerRecord<String, String> record = new ProducerRecord<>(DECIMALS_TOPIC, null, expectedResult.getIntegerValue().toString());

        //when
        producer.send(record);
        producer.flush();

        //then
        var records = consumer.poll(Duration.ofSeconds(5));
        assertNotNull(records);
        assertEquals(1, records.count());
        val iterator = records.iterator();
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        val singleRecord = iterator.next();
        assertEquals(expectedResult, singleRecord.value());
        assertFalse(iterator.hasNext());
    }

    private Consumer<String, NumberInfo> configureConsumer() {
        val consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        val consumer = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), numberInfoDeserializer)
                .createConsumer();
        consumer.subscribe(Collections.singleton(ROMANS_TOPIC));
        return consumer;
    }

    private Producer<String, String> configureProducer() {
        val producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        return new DefaultKafkaProducerFactory<>(producerProps, new StringSerializer(), new StringSerializer())
            .createProducer();
    }
}
