package com.headmostlab.quickbarbell.model.database.entities;

import com.headmostlab.quickbarbell.model.MeasurementUnit;

import java.math.BigDecimal;
import java.util.Arrays;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Disk {

    @PrimaryKey(autoGenerate = true)
    private long diskId;

    private BigDecimal weight;

    private MeasurementUnit unit;

    @ColumnInfo(defaultValue = "0")
    private boolean hidden;

    public Disk() {
    }

    @Ignore
    public Disk(BigDecimal weight) {
        this.weight = weight;
    }

    @Ignore
    public Disk(BigDecimal weight, MeasurementUnit unit) {
        this.weight = weight;
        this.unit = unit;
    }

    public long getDiskId() {
        return diskId;
    }

    public void setDiskId(long diskId) {
        this.diskId = diskId;
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

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Disk)) return false;
        Disk disk = (Disk) o;
        return (weight == disk.weight || weight != null && weight.equals(disk.weight))&&
                unit == disk.unit;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{weight, unit});
    }
}

