package com.headmostlab.quickbarbell.screens.selectassembledbar;

import com.headmostlab.quickbarbell.di.ViewModelScope;

import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Subcomponent;

@ViewModelScope
@Subcomponent(modules = SelectAssembledBarViewModelModule.class)
public interface SelectAssembledBarViewModelComponent {

    ViewModelProvider.Factory presenterFactory();

    @Subcomponent.Factory
    interface Factory {
        SelectAssembledBarViewModelComponent create();
    }

    @Module(subcomponents = SelectAssembledBarViewModelComponent.class)
    interface InstallationModule {
    }
}
