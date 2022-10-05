package com.example.shared.serdes;

import com.example.shared.dtos.NumberInfo;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NumberInfoSerde implements Serde<NumberInfo> {
    private final Serializer<NumberInfo> serializer;
    private final Deserializer<NumberInfo> deserializer;

    @Override
    public Serializer<NumberInfo> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<NumberInfo> deserializer() {
        return deserializer;
    }
}
