package com.headmostlab.quickbarbell.screens.diskinventory;

import com.headmostlab.quickbarbell.BasePresenter;
import com.headmostlab.quickbarbell.BaseView;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListContract;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.DiskCard;

import java.math.BigDecimal;

import androidx.lifecycle.LifecycleOwner;

public interface DiskInventoryContract {
    interface View extends BaseView<Presenter> {
        void shoWeight(BigDecimal weight);

        void showCount(int count);

        LifecycleOwner getLifeCycleOwner();

        void showActionMode();

        void hideActionMode();

        void enableActionModeSelectAllItem(boolean enable);

        void showMessage(int resourceId);

        void showUnit(MeasurementUnit unit);

        void showShop();
    }

    interface Presenter extends BasePresenter<View> {
        void restoreState(String xml);

        String serialize();

        WeightsListContract.Presenter getWeightsListPresenter();

        void deleteSelectedDisks();

        void onSelectionCardsChanged();

        void createDisks();

        void setWeight(BigDecimal weight);

        void setCount(int count);

        void setUnit(MeasurementUnit unit);

        void diskClicked(DiskCard card);
    }
}
