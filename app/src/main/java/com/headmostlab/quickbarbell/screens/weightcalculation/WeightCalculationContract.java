package com.headmostlab.quickbarbell.screens.weightcalculation;

import com.headmostlab.quickbarbell.BasePresenter;
import com.headmostlab.quickbarbell.BaseView;
import com.headmostlab.quickbarbell.business.BunchCalculator;

public interface WeightCalculationContract {
    interface View extends BaseView<Presenter> {
        void showStatus(BunchCalculator.CalcStatus status);
        void showPercent(float percent);
        void enableButton(boolean enable);
        void showMessage(int resourceId);
        void exitScreen();
    }

    interface Presenter extends BasePresenter<View> {
        void startCalculation();
        void pressBack();
    }
}
