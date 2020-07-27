package com.headmostlab.quickbarbell.model;

import android.app.Application;
import android.content.Context;

import com.headmostlab.quickbarbell.TestApp;

import androidx.test.runner.AndroidJUnitRunner;

public class MyCustomTestRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return super.newApplication(cl, TestApp.class.getName(), context);
    }

}
