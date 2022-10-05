package com.example.processor.config;

import com.example.shared.EnableSharedComponents;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSharedComponents
public class ProcessorConfiguration {

    @Bean
    public StreamsConfig streamsConfig(KafkaProperties properties) {
        return new StreamsConfig(properties.buildStreamsProperties());
    }
}
