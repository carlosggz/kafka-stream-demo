package com.example.shared.serdes;

import com.example.shared.dtos.NumberInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NumberInfoSerializer implements Serializer<NumberInfo> {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public byte[] serialize(String topic, NumberInfo numberInfo) {
        return objectMapper.writeValueAsBytes(numberInfo);
    }

    @SneakyThrows
    @Override
    public byte[] serialize(String topic, Headers headers, NumberInfo numberInfo) {
        return objectMapper.writeValueAsBytes(numberInfo);
    }
}
