package com.headmostlab.quickbarbell.di;

import com.headmostlab.quickbarbell.model.database.AppDatabase;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.model.database.dao.BunchDao;
import com.headmostlab.quickbarbell.model.database.dao.BunchDiskLinkDao;
import com.headmostlab.quickbarbell.model.database.dao.BunchWithDisksDao;
import com.headmostlab.quickbarbell.model.database.dao.DiskDao;
import com.headmostlab.quickbarbell.model.database.dao.WeightHistoryDao;
import com.headmostlab.quickbarbell.model.database.dao.WeightTemplateDao;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class DaosModule {

    @ApplicationScope
    @Provides
    static BarDao getBarDao(AppDatabase database) {
        return database.barDao();
    }

    @ApplicationScope
    @Provides
    static DiskDao getDiskDao(AppDatabase database) {
        return database.diskDao();
    }

    @ApplicationScope
    @Provides
    static WeightTemplateDao getWeightTemplateDao(AppDatabase database) {
        return database.weightTemplateDao();
    }

    @ApplicationScope
    @Provides
    static WeightHistoryDao getWeightHistoryDao(AppDatabase database) {
        return database.weightHistoryDao();
    }

    @ApplicationScope
    @Provides
    static BunchDao provideBunchDao(AppDatabase database) {
        return database.bunchDao();
    }

    @ApplicationScope
    @Provides
    static BunchWithDisksDao provideBunchWithDisksDao(AppDatabase database) {
        return database.bunchWithDisksDao();
    }

    @ApplicationScope
    @Provides
    static BunchDiskLinkDao provideBunchDiskLinkDao(AppDatabase database) {
        return database.bunchDiskLinkDao();
    }
}
