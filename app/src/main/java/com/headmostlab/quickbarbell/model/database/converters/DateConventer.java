package com.headmostlab.quickbarbell.model.database.converters;

import java.util.Date;

import androidx.room.TypeConverter;

public class DateConventer {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
