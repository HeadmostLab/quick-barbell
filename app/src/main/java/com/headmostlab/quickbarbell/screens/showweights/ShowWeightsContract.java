package com.headmostlab.quickbarbell.screens.showweights;

import com.headmostlab.quickbarbell.BasePresenter;
import com.headmostlab.quickbarbell.BaseView;
import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListContract;

import androidx.lifecycle.LifecycleOwner;

public interface ShowWeightsContract {

    interface View extends BaseView<Presenter> {
        void showTitle(String title, String subTitle);
        void showAssembledBar(AssembledBar bar);
        LifecycleOwner getLifeCycleOwner();
    }

    interface Presenter extends BasePresenter<View> {
        WeightsListContract.Presenter weightsListPresenter();
        void restoreState(String xml);
        String serialize();
    }

}
