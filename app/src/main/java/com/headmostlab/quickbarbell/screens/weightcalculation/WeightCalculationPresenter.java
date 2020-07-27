package com.headmostlab.quickbarbell.screens.weightcalculation;

import android.os.SystemClock;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.business.BunchCalculator;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

public class WeightCalculationPresenter extends ViewModel implements WeightCalculationContract.Presenter {

    private WeakReference<WeightCalculationContract.View> view;
    private BunchCalculator bunchCalculator;
    private long backTime;

    @Inject
    public WeightCalculationPresenter(BunchCalculator bunchCalculator) {
        this.bunchCalculator = bunchCalculator;
        createSubscriptions();
    }

    private void createSubscriptions() {
        this.bunchCalculator.setOnStatusChangedListener(status -> {
            if (isView()) {
                view().showStatus(status);
                if (status == BunchCalculator.CalcStatus.FINISHED) {
                    view().enableButton(false);
                }
            }
        });
        this.bunchCalculator.setOnProgressChangedListener(percent -> {
            if (isView()) {
                view().showPercent(percent);
            }
        });
    }

    @Override
    public void takeView(@NonNull WeightCalculationContract.View view) {
        this.view = new WeakReference<>(view);

        view().showStatus(bunchCalculator.getCalcStatus());
        view().showPercent(bunchCalculator.getCalcProgress());
    }

    @Override
    public void startCalculation() {
        if (! bunchCalculator.isCalculating()) {
            bunchCalculator.calcWeights();
        } else {
            bunchCalculator.cancel();
        }
    }

    @Override
    public void pressBack() {
        if (bunchCalculator.isCalculating() && timeIntervalBetweenBackPress() > 1000) {
            if (isView()) {
                view().showMessage(R.string.awc_confirm_exit);
            }
        } else {
            if (isView()) {
                view().exitScreen();
            }
        }
    }

    private long timeIntervalBetweenBackPress() {
        long interval = SystemClock.uptimeMillis()-backTime;
        backTime += interval;
        return interval;
    }

    public boolean isView() {
        return view() != null;
    }

    public WeightCalculationContract.View view() {
        return view.get();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        bunchCalculator.cancel();
    }
}