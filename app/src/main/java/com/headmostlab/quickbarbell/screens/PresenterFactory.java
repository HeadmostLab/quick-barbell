package com.headmostlab.quickbarbell.screens;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PresenterFactory<V extends ViewModel> implements ViewModelProvider.Factory {

    private final Provider<V> presenterProvider;

    @Inject
    public PresenterFactory(Provider<V> presenterProvider) {
        this.presenterProvider = presenterProvider;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) presenterProvider.get();
    }

}
