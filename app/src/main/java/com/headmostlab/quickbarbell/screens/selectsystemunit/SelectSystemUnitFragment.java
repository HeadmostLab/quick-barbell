package com.headmostlab.quickbarbell.screens.selectsystemunit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.headmostlab.quickbarbell.App;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.screens.selectbarbell.SelectBarbellFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectSystemUnitFragment extends Fragment implements SelectSystemUnitContract.View {

    @BindView(R.id.dialog)
    View dialog;

    @BindView(R.id.radioGroup)
    RadioGroup unitRadioGroup;

    @BindView(R.id.nextButton)
    View nextButton;

    private SelectSystemUnitContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_system_unit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {

        super.onViewCreated(view, state);

        ButterKnife.bind(this, view);

        presenter = ViewModelProviders.of(this, getPresenterFactory()).get(SelectSystemUnitPresenter.class);

        setupWidgets();

        presenter.takeView(this);
    }

    private ViewModelProvider.Factory getPresenterFactory() {
        return App.getInstance().getAppComponent().selectSystemUnitFactory().create().presenterFactory();
    }

    private void setupWidgets() {
        setupRadioGroup();
    }

    private void setupRadioGroup() {
        unitRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioButtonKilogram:
                    presenter.selectUnit(MeasurementUnit.KILOGRAM);
                    break;
                case R.id.radioButtonPound:
                    presenter.selectUnit(MeasurementUnit.POUND);
                    break;
            }
        });
    }

    @OnClick(R.id.nextButton)
    public void goToNextScreen() {
        presenter.nextScreen();
    }

    @Override
    public void showNextScreen() {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fragment_animation_enter, R.animator.fragment_animation_exit,
                        R.animator.fragment_animation_enter, R.animator.fragment_animation_exit)
                .replace(R.id.rootLayout, new SelectBarbellFragment())
//                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showUnit(MeasurementUnit unit) {
        unitRadioGroup.check(unit == MeasurementUnit.POUND ? R.id.radioButtonPound : R.id.radioButtonKilogram);
    }

}
