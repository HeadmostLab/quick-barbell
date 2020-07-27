package com.headmostlab.quickbarbell.screens.selectsystemunit;

import com.headmostlab.quickbarbell.di.ViewModelScope;
import com.headmostlab.quickbarbell.screens.PresenterFactory;

import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;

@Module
public interface SelectSystemUnitViewModelModule {

    @ViewModelScope
    @Binds
    ViewModelProvider.Factory presenterFactory(PresenterFactory<SelectSystemUnitPresenter> presenterFactory);

    @ViewModelScope
    @Binds SelectSystemUnitContract.Presenter presenter(SelectSystemUnitPresenter presenter);

}
