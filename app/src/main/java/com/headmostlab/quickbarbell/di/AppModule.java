package com.headmostlab.quickbarbell.di;

import android.app.Application;
import android.content.Context;

import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.SettingsModel;
import com.headmostlab.quickbarbell.model.database.AppDatabase;
import com.headmostlab.quickbarbell.model.database.migrations.Migrations;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @ApplicationScope
    @Provides
    static AppDatabase getAppDatabase(Application app) {
        return Room.databaseBuilder(app, AppDatabase.class,"weight")
//                .createFromAsset(app.getString(R.string.asset, "weight"))
                .allowMainThreadQueries()
//                .fallbackToDestructiveMigration()
                .addMigrations(Migrations.ALL)
                .build();
    }

    @Provides
    static MeasurementUnit provideSystemUnit(SettingsModel settingsModel) {
        return settingsModel.getUnit();
    }

    @Provides
    static Context provideContext(Application application) {
        return application;
    }

}
