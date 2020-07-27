package com.headmostlab.quickbarbell.screens.selectsystemunit;

import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.SettingsModel;
import com.headmostlab.quickbarbell.model.dbfillers.DbFiller;
import com.headmostlab.quickbarbell.model.dbfillers.KgDbFiller;
import com.headmostlab.quickbarbell.model.dbfillers.LbDbFiller;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

public class SelectSystemUnitPresenter extends ViewModel implements SelectSystemUnitContract.Presenter {

    private final SettingsModel settingsModel;
    private final KgDbFiller kgFiller;
    private final LbDbFiller lbFiller;
    private WeakReference<SelectSystemUnitContract.View> view;
    private MeasurementUnit systemUnit = MeasurementUnit.KILOGRAM;

    @Inject
    public SelectSystemUnitPresenter(SettingsModel settingsModel, KgDbFiller kgFiller, LbDbFiller lbFiller) {
        this.settingsModel = settingsModel;
        this.kgFiller = kgFiller;
        this.lbFiller = lbFiller;
    }

    @Override
    public void takeView(@NonNull SelectSystemUnitContract.View view) {
        this.view = new WeakReference<>(view);
        view().showUnit(systemUnit);
    }

    @Override
    public SelectSystemUnitContract.View view() {
        return view.get();
    }

    @Override
    public boolean isView() {
        return view() != null;
    }

    @Override
    public void nextScreen() {
        getDbFiller().fill();
        settingsModel.setUnit(systemUnit);
        settingsModel.prepareDb();
        view().showNextScreen();
    }

    @Override
    public void selectUnit(MeasurementUnit unit) {
        systemUnit = unit;
    }

    private DbFiller getDbFiller() {
        return (systemUnit == MeasurementUnit.KILOGRAM) ? kgFiller : lbFiller;
    }

}
