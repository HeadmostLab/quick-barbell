package com.headmostlab.quickbarbell.screens.selectweight;

import com.headmostlab.quickbarbell.di.ViewModelScope;

import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Subcomponent;

@ViewModelScope
@Subcomponent(modules = SelectWeightViewModelModule.class)
public interface SelectWeightViewModelComponent {

    ViewModelProvider.Factory presenterFactory();

    @Subcomponent.Factory
    interface Factory {
        SelectWeightViewModelComponent create();
    }

    @Module(subcomponents = SelectWeightViewModelComponent.class)
    interface InstallationModule {
    }
}
