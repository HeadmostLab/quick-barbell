package com.headmostlab.quickbarbell.screens.showweights;

import com.headmostlab.quickbarbell.di.ViewModelScope;
import com.headmostlab.quickbarbell.screens.PresenterFactory;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListContract;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListPresenter;

import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;

@Module
public interface ShowWeightsViewModelModule {

    @ViewModelScope
    @Binds
    ViewModelProvider.Factory showWeightsPresenterFactory(PresenterFactory<ShowWeightsPresenter> presenterFactory);

    @ViewModelScope
    @Binds
    ShowWeightsContract.Presenter selectWeightPresenter(ShowWeightsPresenter presenter);

    @ViewModelScope
    @Binds
    WeightsListContract.Presenter provideWeightsListPresenter(WeightsListPresenter presenter);
}
