package com.headmostlab.quickbarbell.model.database.entities;

import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.utils.BigDecimalUtils;

import java.math.BigDecimal;
import java.util.Arrays;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Bar {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private BigDecimal weight;

    private MeasurementUnit unit;

    private BarTypes barType;

    public Bar() {
    }

    @Ignore
    public Bar(BarTypes barType, BigDecimal weight) {
        this.barType = barType;
        this.weight = weight;
    }

    @Ignore
    public Bar(BarTypes barType, BigDecimal weight, MeasurementUnit unit) {
        this.barType = barType;
        this.weight = weight;
        this.unit = unit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public BigDecimal getWeightInKg() {
        return unit.equals(MeasurementUnit.KILOGRAM) ? weight : weight.multiply(unit.getCoeff());
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public MeasurementUnit getUnit() {
        return unit;
    }

    public void setUnit(MeasurementUnit unit) {
        this.unit = unit;
    }

    public BarTypes getBarType() {
        return barType;
    }

    public void setBarType(BarTypes barType) {
        this.barType = barType;
    }

    public String getWeightWithUnit() {
        return BigDecimalUtils.toString(weight)+unit.getLabel();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bar)) return false;
        Bar bar = (Bar) o;

        return (weight == bar.weight || weight != null && weight.equals(bar.weight)) &&
                unit == bar.unit &&
                barType == bar.barType;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{weight, unit, barType});
    }
}
