package com.headmostlab.quickbarbell.screens.settings;

import com.headmostlab.quickbarbell.di.ViewModelScope;

import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Subcomponent;

@ViewModelScope
@Subcomponent(modules = SettingsViewModelModule.class)
public interface SettingsViewModelComponent {

    ViewModelProvider.Factory presenterFactory();

    @Subcomponent.Factory
    interface Factory {
        SettingsViewModelComponent create();
    }

    @Module(subcomponents = SettingsViewModelComponent.class)
    interface InstallationModule {
    }
}
