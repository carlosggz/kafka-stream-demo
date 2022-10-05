package com.example.processor.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RomanConverterTest {

    private final RomanConverter converter = new RomanConverter();

    @Test
    void invalidNumberThrowException() {
        assertThrows(IllegalArgumentException.class, () -> converter.toRoman(null));
        assertThrows(IllegalArgumentException.class, () -> converter.toRoman(-1));
        assertThrows(IllegalArgumentException.class, () -> converter.toRoman(0));
        assertThrows(IllegalArgumentException.class, () -> converter.toRoman(4000));
    }

    @ParameterizedTest
    @MethodSource("values")
    void shouldGenerateARomanNumberBasedOnADecimalValue(int givenNumber, String expectedRoman) {
        val result = converter.toRoman(givenNumber);
        assertNotNull(result);
        assertEquals(givenNumber, result.getIntegerValue());
        assertEquals(expectedRoman, result.getRomanNumber());
    }

    static Stream<Arguments> values() {
        return Stream.of(
            Arguments.of(1, "I"),
            Arguments.of(4, "IV"),
            Arguments.of(8, "VIII"),
            Arguments.of(9, "IX"),
            Arguments.of(50, "L"),
            Arguments.of(1234, "MCCXXXIV")
        );
    }
}
