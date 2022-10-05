package com.example.processor.components;

import com.example.shared.dtos.NumberInfo;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class RomanConverter {

    private static final String[] THOUSANDS = {"", "M", "MM", "MMM"};
    private static final String[] HUNDREDS = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
    private static final String[] TENS = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
    private static final String[] UNITS = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

    public NumberInfo toRoman(Integer number) {
        Assert.notNull(number, "Invalid value");
        Assert.isTrue(number > 0 && number < 3999, "Invalid value");
        var result = THOUSANDS[number / 1000] +
            HUNDREDS[(number % 1000) / 100] +
            TENS[(number % 100) / 10] +
            UNITS[number % 10];
        return new NumberInfo(number, result);
    }
}

