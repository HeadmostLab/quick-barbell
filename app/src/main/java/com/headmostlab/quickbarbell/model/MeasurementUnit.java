package com.headmostlab.quickbarbell.model;

import java.math.BigDecimal;

public enum MeasurementUnit {
    KILOGRAM(BigDecimal.ONE, "kg", BigDecimal.valueOf(50)),
    POUND(new BigDecimal("0.453592"), "lb", BigDecimal.valueOf(110));

    private BigDecimal weightCoefficient;
    private String label;
    private BigDecimal maxWeight;

    MeasurementUnit(BigDecimal weightCoefficient, String label, BigDecimal maxWeight) {
        this.weightCoefficient = weightCoefficient;
        this.label = label;
        this.maxWeight = maxWeight;
    }

    public BigDecimal getCoeff() {
        return weightCoefficient;
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getMaxWeight() {
        return maxWeight;
    }
}
