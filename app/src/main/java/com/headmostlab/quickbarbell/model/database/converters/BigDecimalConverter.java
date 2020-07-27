package com.headmostlab.quickbarbell.model.database.converters;

import java.math.BigDecimal;
import java.math.MathContext;

import androidx.room.TypeConverter;

public class BigDecimalConverter {

    private static final BigDecimal hundred = BigDecimal.valueOf(100);

    @TypeConverter
    public long fromType (BigDecimal decimal) {
        return decimal.multiply(hundred).longValue();
    }

    @TypeConverter
    public BigDecimal toType (long decimal) {
        return BigDecimal.valueOf(decimal).divide(hundred, MathContext.DECIMAL32);
    }

}
