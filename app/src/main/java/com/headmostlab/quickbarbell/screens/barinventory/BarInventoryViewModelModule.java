package com.headmostlab.quickbarbell.screens.barinventory;

import com.headmostlab.quickbarbell.di.ViewModelScope;
import com.headmostlab.quickbarbell.screens.PresenterFactory;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListContract;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListPresenter;

import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;

@Module
public interface BarInventoryViewModelModule {

    @ViewModelScope
    @Binds
    ViewModelProvider.Factory barInventoryPresenterFactory(PresenterFactory<BarInventoryPresenter> presenterFactory);

    @ViewModelScope
    @Binds BarInventoryContract.Presenter barInventoryPresenter(BarInventoryPresenter presenter);

    @ViewModelScope
    @Binds WeightsListContract.Presenter weightsListPresenter(WeightsListPresenter presenter);
}
