package com.headmostlab.quickbarbell.screens.weighthistory;

import com.headmostlab.quickbarbell.di.ViewModelScope;
import com.headmostlab.quickbarbell.screens.PresenterFactory;

import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;

@Module
public interface WeightHistoryViewModelModule {

    @ViewModelScope
    @Binds
    ViewModelProvider.Factory weightHistoryFactory(PresenterFactory<WeightHistoryPresenter> presenterFactory);

    @ViewModelScope
    @Binds WeightHistoryContract.Presenter weightHistoryPresenter(WeightHistoryPresenter presenter);
}
