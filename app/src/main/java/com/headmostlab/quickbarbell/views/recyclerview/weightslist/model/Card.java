package com.headmostlab.quickbarbell.views.recyclerview.weightslist.model;

import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.utils.BigDecimalUtils;

import java.math.BigDecimal;

public abstract class Card {
    protected BigDecimal weight;
    protected MeasurementUnit unit;
    protected boolean isShowUnit;

    public Card(BigDecimal weight, MeasurementUnit unit, boolean isShowUnit) {
        this.weight = weight;
        this.unit = unit;
        this.isShowUnit = isShowUnit;
    }

    @Override
    public String toString() {
        return BigDecimalUtils.toString(weight)+(isShowUnit?unit.getLabel():"");
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public void setUnit(MeasurementUnit unit) {
        this.unit = unit;
    }

    public MeasurementUnit getUnit() {
        return unit;
    }

    public BigDecimal getWeightInKg() {
        return unit.equals(MeasurementUnit.KILOGRAM) ? weight : weight.multiply(unit.getCoeff());
    }
}
