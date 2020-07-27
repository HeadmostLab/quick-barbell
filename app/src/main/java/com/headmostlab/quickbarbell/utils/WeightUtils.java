package com.headmostlab.quickbarbell.utils;

import com.headmostlab.quickbarbell.model.MeasurementUnit;

import java.math.BigDecimal;
import java.math.MathContext;

public class WeightUtils {
    public static BigDecimal convert(
            BigDecimal weight, MeasurementUnit weightUnit, MeasurementUnit desiredUnit)
    {
        return weightUnit.equals(desiredUnit) ?
                weight :
                weight.multiply(weightUnit.getCoeff())
                        .divide(desiredUnit.getCoeff(), MathContext.DECIMAL64);
    }

    public static BigDecimal convertKgTo(BigDecimal weightInKg, MeasurementUnit desiredUnit) {
        return convert(weightInKg, MeasurementUnit.KILOGRAM, desiredUnit);
    }
}
