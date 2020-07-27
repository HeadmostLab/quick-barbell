package com.headmostlab.quickbarbell.screens.weighthistory;

import com.headmostlab.quickbarbell.di.ViewModelScope;

import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Subcomponent;

@ViewModelScope
@Subcomponent(modules = WeightHistoryViewModelModule.class)
public interface WeightHistoryViewModelComponent {

    ViewModelProvider.Factory presenterFactory();

    @Subcomponent.Factory
    interface Factory {
        WeightHistoryViewModelComponent create();
    }

    @Module(subcomponents = WeightHistoryViewModelComponent.class)
    interface InstallationModule {
    }
}
