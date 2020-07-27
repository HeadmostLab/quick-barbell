package com.headmostlab.quickbarbell.screens.settings;

import com.headmostlab.quickbarbell.di.ViewModelScope;
import com.headmostlab.quickbarbell.screens.PresenterFactory;

import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;

@Module
public interface SettingsViewModelModule {

    @ViewModelScope
    @Binds
    ViewModelProvider.Factory settingsPresenterFactory(PresenterFactory<SettingsPresenter> presenterFactory);

    @ViewModelScope
    @Binds
    SettingsContract.Presenter settingsPresenter(SettingsPresenter presenter);
}
