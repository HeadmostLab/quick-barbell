package com.headmostlab.quickbarbell.screens.weightcalculation;

import com.headmostlab.quickbarbell.di.ViewModelScope;

import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Subcomponent;

@ViewModelScope
@Subcomponent(modules = WeightCalculationViewModelModule.class)
public interface WeightCalculationViewModelComponent {

    ViewModelProvider.Factory presenterFactory();

    @Subcomponent.Factory
    interface Factory {
        WeightCalculationViewModelComponent create();
    }

    @Module(subcomponents = WeightCalculationViewModelComponent.class)
    interface InstallationModule {
    }
}
