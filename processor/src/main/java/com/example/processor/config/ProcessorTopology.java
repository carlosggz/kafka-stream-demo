package com.example.processor.config;

import com.example.processor.components.RomanConverter;
import com.example.shared.dtos.NumberInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Configuration
@EnableKafkaStreams
@Slf4j
public class ProcessorTopology {

    @Bean
    public Topology transformToRomaTopology(
        final StreamsBuilder streamsBuilder,
        final RomanConverter romanConverter,
        final Serde<NumberInfo> numberInfoSerde,
        @Value("${app.decimal-topic}") final String decimalTopic,
        @Value("${app.roman-topic}") final String romansTopic) {
        streamsBuilder
            .stream(decimalTopic, Consumed.with(Serdes.String(), Serdes.String()))
            .mapValues((key, value) -> romanConverter.toRoman(Integer.valueOf(value)))
            .peek((key, value) -> log.info("Converted value {} to {}", value.getIntegerValue(), value.getRomanNumber()))
            .to(romansTopic, Produced.with(Serdes.String(), numberInfoSerde));

        return streamsBuilder.build();
    }
}
