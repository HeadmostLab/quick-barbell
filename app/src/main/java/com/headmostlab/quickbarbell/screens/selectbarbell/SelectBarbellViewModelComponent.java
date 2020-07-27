package com.headmostlab.quickbarbell.screens.selectbarbell;

import com.headmostlab.quickbarbell.di.ViewModelScope;

import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Subcomponent;

@ViewModelScope
@Subcomponent(modules = SelectBarbellViewModelModule.class)
public interface SelectBarbellViewModelComponent {

    ViewModelProvider.Factory presenterFactory();

    @Subcomponent.Factory
    interface Factory {
        SelectBarbellViewModelComponent create();
    }

    @Module(subcomponents = SelectBarbellViewModelComponent.class)
    interface InstallationModule {
    }
}
