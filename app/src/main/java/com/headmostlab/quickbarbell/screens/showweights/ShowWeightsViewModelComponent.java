package com.headmostlab.quickbarbell.screens.showweights;

import com.headmostlab.quickbarbell.di.ViewModelScope;

import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Subcomponent;

@ViewModelScope
@Subcomponent(modules = ShowWeightsViewModelModule.class)
public interface ShowWeightsViewModelComponent {

    ViewModelProvider.Factory presenterFactory();

    @Subcomponent.Factory
    interface Factory {
        ShowWeightsViewModelComponent create();
    }

    @Module(subcomponents = ShowWeightsViewModelComponent.class)
    interface InstallationModule {
    }
}
