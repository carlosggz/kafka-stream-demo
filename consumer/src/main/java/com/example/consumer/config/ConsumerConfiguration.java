package com.example.consumer.config;

import com.example.shared.EnableSharedComponents;
import com.example.shared.dtos.NumberInfo;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
@Slf4j
@EnableSharedComponents
public class ConsumerConfiguration {

    @Bean
    public Consumer<Message<NumberInfo>> obtainNumberInfo() {
        return value -> {
            log.info("Consuming value: {} in thread {}", value.getPayload(), Thread.currentThread().getName());
        };
    }

    @Bean
    public StreamsConfig streamsConfig(KafkaProperties properties) {
        return new StreamsConfig(properties.buildStreamsProperties());
    }
}
