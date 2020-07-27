package com.headmostlab.quickbarbell.screens.diskinventory;

import com.headmostlab.quickbarbell.di.ViewModelScope;

import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Subcomponent;

@ViewModelScope
@Subcomponent(modules = DiskInventoryViewModelModule.class)
public interface DiskInventoryViewModelComponent {

    ViewModelProvider.Factory presenterFactory();

    @Subcomponent.Factory
    interface Factory {
        DiskInventoryViewModelComponent create();
    }

    @Module(subcomponents = DiskInventoryViewModelComponent.class)
    interface InstallationModule {
    }
}
