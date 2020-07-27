package com.headmostlab.quickbarbell.model.database.converters;

import com.headmostlab.quickbarbell.business.BarTypes;

import androidx.room.TypeConverter;

public class BarTypeConverter {

    @TypeConverter
    public int fromBarType (BarTypes barType) {
        return barType.ordinal();
    }

    @TypeConverter
    public BarTypes toBarType(int barTypeId) {
        return BarTypes.values()[barTypeId];
    }
}
