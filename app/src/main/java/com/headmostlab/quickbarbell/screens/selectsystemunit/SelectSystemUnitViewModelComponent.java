package com.headmostlab.quickbarbell.screens.selectsystemunit;

import com.headmostlab.quickbarbell.di.ViewModelScope;

import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Subcomponent;

@ViewModelScope
@Subcomponent(modules = SelectSystemUnitViewModelModule.class)
public interface SelectSystemUnitViewModelComponent {

    ViewModelProvider.Factory presenterFactory();

    @Subcomponent.Factory
    interface Factory {
        SelectSystemUnitViewModelComponent create();
    }

    @Module(subcomponents = SelectSystemUnitViewModelComponent.class)
    interface InstallationModule {
    }
}
