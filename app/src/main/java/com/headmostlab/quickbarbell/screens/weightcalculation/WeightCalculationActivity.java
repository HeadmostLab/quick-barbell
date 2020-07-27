package com.headmostlab.quickbarbell.screens.weightcalculation;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.headmostlab.quickbarbell.App;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.business.BunchCalculator;
import com.headmostlab.quickbarbell.views.HLSpinnerProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WeightCalculationActivity extends AppCompatActivity implements WeightCalculationContract.View {

    @BindView(R.id.textStartStop)
    TextView mStartStop;

    @BindView(R.id.description)
    TextView mDescription;

    @BindView(R.id.progressBar)
    HLSpinnerProgressBar mProgressBar;

    @BindView(R.id.coordinatorLayout)
    View coordinatorLayout;

    private WeightCalculationContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(App.getInstance().getSettingsModel().getTheme().themeId);

        setContentView(R.layout.activity_weight_calculation);
        ButterKnife.bind(this);

        setUpWidgets();

        presenter = ViewModelProviders.of(this, getPresenterFactory()).get(WeightCalculationPresenter.class);
        presenter.takeView(this);
    }

    private void setUpWidgets() {
        mDescription.setText(Html.fromHtml(getString(R.string.awc_description)));
    }

    private ViewModelProvider.Factory getPresenterFactory() {
        return App.getInstance().getAppComponent().weightCalculationFactory().create().presenterFactory();
    }

    @Override
    public void showStatus(BunchCalculator.CalcStatus status) {
        String textStatus;
        switch (status) {
            case CALCULATING:
                textStatus = getString(R.string.awc_button_cancel);
                break;
            case FINISHED:
                textStatus = getString(R.string.awc_button_done);
                break;
            default:
                textStatus = getString(R.string.awc_button_start);

        }
        mStartStop.setText(textStatus);
    }

    @Override
    public void showPercent(float percent) {
        mProgressBar.setProgress(percent);
    }

    @Override
    public void enableButton(boolean enable) {
        mStartStop.setClickable(enable);
    }

    @Override
    public void showMessage(int resourceId) {
        Snackbar.make(coordinatorLayout, resourceId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void exitScreen() {
        finish();
    }

    @OnClick(R.id.textStartStop)
    public void startStopWeightCalc(View view) {
        presenter.startCalculation();
    }

    @Override
    public void onBackPressed() {
        presenter.pressBack();
    }
}