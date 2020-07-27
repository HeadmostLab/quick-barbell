package com.headmostlab.quickbarbell;

import android.app.Application;

import com.headmostlab.quickbarbell.business.billing.BillingRepository;
import com.headmostlab.quickbarbell.di.AppComponent;
import com.headmostlab.quickbarbell.di.DaggerAppComponent;
import com.headmostlab.quickbarbell.business.TransitData;
import com.headmostlab.quickbarbell.model.SettingsModel;
import com.headmostlab.quickbarbell.model.database.AppDatabase;

import javax.inject.Inject;

public class App extends Application {

    private static App instance;

    @Inject
    AppDatabase database;

    @Inject
    TransitData transitData;

    @Inject
    SettingsModel settingsModel;

    @Inject
    BillingRepository billingRepository;

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        initializeComponent();

        instance = this;
    }

    protected void initializeComponent() {
        appComponent = DaggerAppComponent.builder().application(this).build();
        appComponent.inject(this);

        billingRepository.startConnection();
    }

    public static App getInstance() {
        return instance;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public TransitData getTransitData() {
        return transitData;
    }

    public SettingsModel getSettingsModel() {
        return settingsModel;
    }

}
