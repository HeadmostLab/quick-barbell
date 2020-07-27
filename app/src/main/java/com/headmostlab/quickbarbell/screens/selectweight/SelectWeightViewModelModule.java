package com.headmostlab.quickbarbell.screens.selectweight;

import com.headmostlab.quickbarbell.di.ViewModelScope;
import com.headmostlab.quickbarbell.screens.PresenterFactory;
import com.headmostlab.quickbarbell.views.recyclerview.memoryview.MemoryListContract;
import com.headmostlab.quickbarbell.views.recyclerview.memoryview.MemoryListPresenter;

import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;

@Module
public interface SelectWeightViewModelModule {

    @ViewModelScope
    @Binds
    ViewModelProvider.Factory selectWeightPresenterFactory(PresenterFactory<SelectWeightPresenter> presenterFactory);

    @ViewModelScope
    @Binds
    SelectWeightContract.Presenter selectWeightPresenter(SelectWeightPresenter presenter);

    @ViewModelScope
    @Binds
    MemoryListContract.Presenter memoryListPresenter(MemoryListPresenter presenter);

}
