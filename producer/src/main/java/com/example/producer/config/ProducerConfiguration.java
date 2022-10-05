package com.example.producer.config;

import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@Configuration
@Slf4j
public class ProducerConfiguration {

    public static final int START_VALUE = 1;
    public static final int END_VALUE = 10; //after 4k the notation changes

    @Bean
    public Supplier<Message<String>> generateNumber() {
        return () -> {
            var message = MessageBuilder
                .withPayload(String.valueOf(RandomUtils.nextInt(START_VALUE, END_VALUE+1)))
                .build();
            log.info("Sending number: {}", message.getPayload());
            return message;
        };
    }
}

