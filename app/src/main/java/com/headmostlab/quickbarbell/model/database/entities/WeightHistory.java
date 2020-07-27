package com.headmostlab.quickbarbell.model.database.entities;

import com.headmostlab.quickbarbell.model.MeasurementUnit;

import java.math.BigDecimal;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = WeightTemplate.class, parentColumns = "id", childColumns = "templateId",
        onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE))
public class WeightHistory {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(index = true)
    private long templateId;

    @NonNull
    private Date date;

    @NonNull
    private BigDecimal weight;

    @NonNull
    private MeasurementUnit unit;

    public WeightHistory(long templateId, Date date, BigDecimal weight, MeasurementUnit unit) {
        this.templateId = templateId;
        this.date = date;
        this.weight = weight;
        this.unit = unit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getWeight() {
        return weight;
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
}
