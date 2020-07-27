package com.headmostlab.quickbarbell.screens.diskinventory;

import com.headmostlab.quickbarbell.di.ViewModelScope;
import com.headmostlab.quickbarbell.screens.PresenterFactory;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListContract;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListPresenter;

import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;

@Module
public interface DiskInventoryViewModelModule {

    @ViewModelScope
    @Binds
    ViewModelProvider.Factory diskInventoryPresenterFactory(PresenterFactory<DiskInventoryPresenter> presenterFactory);

    @ViewModelScope
    @Binds DiskInventoryContract.Presenter diskInventoryPresenter(DiskInventoryPresenter presenter);

    @ViewModelScope
    @Binds WeightsListContract.Presenter weightsListPresenter(WeightsListPresenter presenter);
}
