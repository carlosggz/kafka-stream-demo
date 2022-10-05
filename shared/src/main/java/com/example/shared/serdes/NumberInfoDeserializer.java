package com.example.shared.serdes;

import com.example.shared.dtos.NumberInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NumberInfoDeserializer implements Deserializer<NumberInfo> {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public NumberInfo deserialize(String topic, byte[] data) {
        return objectMapper.readValue(data, NumberInfo.class);
    }

    @SneakyThrows
    @Override
    public NumberInfo deserialize(String topic, Headers headers, byte[] data) {
        return objectMapper.readValue(data, NumberInfo.class);
    }
}
