package com.example.producer;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.producer.config.ProducerConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = {
    "app.decimal-topic=my-topic",
    "app.poller-delay=1s"
})
@Import(TestChannelBinderConfiguration.class)
class ProducerApplicationTests {

    private static final String TOPIC_NAME = "my-topic";
    static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private OutputDestination outputDestination;

    @Test
    void contextLoads() {
        outputDestination.clear(TOPIC_NAME);
    }

    @Test
    void shouldRaiseMessage() {
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            val message = outputDestination.receive(1000, TOPIC_NAME);
            assertNotNull(message);
            val value = objectMapper.readValue(message.getPayload(), String.class);
            assertNotNull(value);
            var valueInt = Integer.valueOf(value);
            assertTrue(valueInt >= ProducerConfiguration.START_VALUE && valueInt <= ProducerConfiguration.END_VALUE);
        });
    }
}
