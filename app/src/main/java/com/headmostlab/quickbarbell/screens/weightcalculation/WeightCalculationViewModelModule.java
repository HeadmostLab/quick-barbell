package com.headmostlab.quickbarbell.screens.weightcalculation;

import com.headmostlab.quickbarbell.di.ViewModelScope;
import com.headmostlab.quickbarbell.screens.PresenterFactory;

import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;

@Module
public interface WeightCalculationViewModelModule {

    @ViewModelScope
    @Binds
    ViewModelProvider.Factory WeightCalculationPresenterFactory(PresenterFactory<WeightCalculationPresenter> presenterFactory);

    @ViewModelScope
    @Binds WeightCalculationContract.Presenter WeightCalculationPresenter(WeightCalculationPresenter presenter);
}
