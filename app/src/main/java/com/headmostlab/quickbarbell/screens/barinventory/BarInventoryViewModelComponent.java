package com.headmostlab.quickbarbell.screens.barinventory;

import com.headmostlab.quickbarbell.di.ViewModelScope;

import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Subcomponent;

@ViewModelScope
@Subcomponent(modules = BarInventoryViewModelModule.class)
public interface BarInventoryViewModelComponent {

    ViewModelProvider.Factory presenterFactory();

    @Subcomponent.Factory
    interface Factory {
        BarInventoryViewModelComponent create();
    }

    @Module(subcomponents = BarInventoryViewModelComponent.class)
    interface InstallationModule {
    }
}
