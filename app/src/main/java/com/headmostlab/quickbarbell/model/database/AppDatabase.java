package com.headmostlab.quickbarbell.model.database;

import com.headmostlab.quickbarbell.model.database.converters.BarTypeConverter;
import com.headmostlab.quickbarbell.model.database.converters.BigDecimalConverter;
import com.headmostlab.quickbarbell.model.database.converters.DateConventer;
import com.headmostlab.quickbarbell.model.database.converters.MeasurementUnitConverter;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.model.database.dao.BunchDao;
import com.headmostlab.quickbarbell.model.database.dao.BunchDiskLinkDao;
import com.headmostlab.quickbarbell.model.database.dao.BunchWithDisksDao;
import com.headmostlab.quickbarbell.model.database.dao.DiskDao;
import com.headmostlab.quickbarbell.model.database.dao.WeightHistoryDao;
import com.headmostlab.quickbarbell.model.database.dao.WeightTemplateDao;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.model.database.entities.Bunch;
import com.headmostlab.quickbarbell.model.database.entities.BunchDiskLink;
import com.headmostlab.quickbarbell.model.database.entities.Disk;
import com.headmostlab.quickbarbell.model.database.entities.WeightHistory;
import com.headmostlab.quickbarbell.model.database.entities.WeightTemplate;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Bar.class, WeightTemplate.class, WeightHistory.class, Disk.class, BunchDiskLink.class, Bunch.class},
        version = 6)
@TypeConverters({BigDecimalConverter.class, MeasurementUnitConverter.class, BarTypeConverter.class, DateConventer.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract BarDao barDao();

    public abstract DiskDao diskDao();

    public abstract BunchDao bunchDao();

    public abstract BunchWithDisksDao bunchWithDisksDao();

    public abstract BunchDiskLinkDao bunchDiskLinkDao();

    public abstract WeightTemplateDao weightTemplateDao();

    public abstract WeightHistoryDao weightHistoryDao();

}