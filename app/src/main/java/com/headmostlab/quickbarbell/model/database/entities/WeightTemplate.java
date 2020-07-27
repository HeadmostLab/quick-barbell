package com.headmostlab.quickbarbell.model.database.entities;

import com.google.common.base.Objects;
import com.headmostlab.quickbarbell.model.MeasurementUnit;

import java.math.BigDecimal;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Bar.class, parentColumns = "id", childColumns = "barid",
        onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE))
public class WeightTemplate {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(index = true)
    private long barid;

    private BigDecimal weight;

    private MeasurementUnit unit;

    private float percent;

    private String comment;

    private boolean balanced;

    public WeightTemplate() {
    }

    @Ignore
    public WeightTemplate(long id, long barid, BigDecimal weight, MeasurementUnit unit, float percent, String comment) {
        this(barid, weight, unit, percent, comment, true);
        this.id = id;
    }

    @Ignore
    public WeightTemplate(long barid, BigDecimal weight, MeasurementUnit unit, float percent, String comment, boolean balanced) {
        this.barid = barid;
        this.weight = weight;
        this.percent = percent;
        this.comment = comment;
        this.unit = unit;
        this.balanced = balanced;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBarid() {
        return barid;
    }

    public void setBarid(long barid) {
        this.barid = barid;
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

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isBalanced() {
        return balanced;
    }

    public void setBalanced(boolean balanced) {
        this.balanced = balanced;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeightTemplate template = (WeightTemplate) o;
        return id == template.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
