package com.headmostlab.quickbarbell.screens.selectassembledbar;

import com.headmostlab.quickbarbell.di.ViewModelScope;
import com.headmostlab.quickbarbell.screens.PresenterFactory;

import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;

@Module
public interface SelectAssembledBarViewModelModule {


    @ViewModelScope
    @Binds
    ViewModelProvider.Factory selectAssembledBarPresenterFactory(PresenterFactory<SelectAssembledBarPresenter> presenterFactory);

    @ViewModelScope
    @Binds SelectAssembledBarContract.Presenter selectAssembledBarPresenter(SelectAssembledBarPresenter presenter);

}
