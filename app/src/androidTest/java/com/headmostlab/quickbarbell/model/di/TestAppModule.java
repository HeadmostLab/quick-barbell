package com.headmostlab.quickbarbell.model.di;

import android.app.Application;

import com.headmostlab.quickbarbell.di.ApplicationScope;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.SettingsModel;
import com.headmostlab.quickbarbell.model.database.AppDatabase;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module
public class TestAppModule {

    @ApplicationScope
    @Provides
    static SettingsModel getSettingsModel(Application app) {
        return new SettingsModel(app);
    }

    @ApplicationScope
    @Provides
    static AppDatabase getAppDatabase(Application app) {
        return Room.inMemoryDatabaseBuilder(app, AppDatabase.class).allowMainThreadQueries().build();
    }

    @Provides
    static MeasurementUnit provideSystemUnit(/*SettingsModel settingsModel*/) {
        return MeasurementUnit.KILOGRAM; //settingsModel.getUnit();
    }
}
