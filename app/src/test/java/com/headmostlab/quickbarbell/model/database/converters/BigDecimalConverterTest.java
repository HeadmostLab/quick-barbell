package com.headmostlab.quickbarbell.model.database.converters;

import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class BigDecimalConverterTest {

    @Test
    public void fromType() {
        BigDecimalConverter converter = new BigDecimalConverter();
        long fromType = converter.fromType(new BigDecimal("123.459"));
        assertThat(fromType).isEqualTo(12345);
    }

    @Test
    public void toType() {
        BigDecimalConverter converter = new BigDecimalConverter();
        final BigDecimal bigDecimal = converter.toType(12345);
        assertThat(bigDecimal.toString()).isEqualTo("123.45");
    }
}