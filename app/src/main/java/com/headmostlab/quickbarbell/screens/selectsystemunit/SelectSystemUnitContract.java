package com.headmostlab.quickbarbell.screens.selectsystemunit;

import com.headmostlab.quickbarbell.BasePresenter;
import com.headmostlab.quickbarbell.BaseView;
import com.headmostlab.quickbarbell.model.MeasurementUnit;

import androidx.lifecycle.LifecycleObserver;

public interface SelectSystemUnitContract {

    interface View extends BaseView<Presenter> {
        void showNextScreen();
        void showUnit(MeasurementUnit unit);
    }

    interface Presenter extends BasePresenter<View>, LifecycleObserver {
        void nextScreen();
        void selectUnit(MeasurementUnit unit);
    }

}
