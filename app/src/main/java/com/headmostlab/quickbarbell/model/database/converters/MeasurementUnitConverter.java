package com.headmostlab.quickbarbell.model.database.converters;

import com.headmostlab.quickbarbell.model.MeasurementUnit;

import androidx.room.TypeConverter;

public class MeasurementUnitConverter {

    @TypeConverter
    public int fromBarType (MeasurementUnit unit) {
        return unit.ordinal();
    }

    @TypeConverter
    public MeasurementUnit toBarType(int unitId) {
        return MeasurementUnit.values()[unitId];
    }
}
