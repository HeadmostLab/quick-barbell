package com.headmostlab.quickbarbell;

import com.headmostlab.quickbarbell.model.di.DaggerTestAppComponent;
import com.headmostlab.quickbarbell.model.di.TestAppComponent;

public class TestApp extends App {

    private TestAppComponent appInjector;

    @Override
    protected void initializeComponent() {
        appInjector = DaggerTestAppComponent.builder().application(this).build();
        appInjector.inject(this);
    }

    public TestAppComponent getAppInjector() {
        return appInjector;
    }
}
