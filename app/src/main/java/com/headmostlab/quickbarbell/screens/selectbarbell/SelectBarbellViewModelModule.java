package com.headmostlab.quickbarbell.screens.selectbarbell;

import com.headmostlab.quickbarbell.di.ViewModelScope;
import com.headmostlab.quickbarbell.screens.PresenterFactory;

import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;

@Module
public interface SelectBarbellViewModelModule {

    @ViewModelScope
    @Binds
    ViewModelProvider.Factory selectBarbellPresenterFactory(PresenterFactory<SelectBarbellPresenter> presenterFactory);

    @ViewModelScope
    @Binds SelectBarbellContract.Presenter selectBarbellPresenter(SelectBarbellPresenter presenter);

}
