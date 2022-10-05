package com.example.consumer.config;

import com.example.shared.dtos.NumberInfo;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Configuration
@EnableKafkaStreams
public class ConsumerTopology {
    @Bean
    public Topology readFromTableTopology(
        final StreamsBuilder streamsBuilder,
        final Serde<NumberInfo> numberInfoSerde,
        @Value("${app.roman-topic}") final String romansTopic,
        @Value("${app.store-topic}") final String numbersStore) {

        streamsBuilder
            .stream(romansTopic, Consumed.with(Serdes.String(), numberInfoSerde))
            .map((key, value) -> new KeyValue<>(value.getRomanNumber(), value))
            .groupByKey(Grouped.with(Serdes.String(), numberInfoSerde))
            .count(Materialized.as(numbersStore));

        return streamsBuilder.build();
    }
}
