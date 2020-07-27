package com.headmostlab.quickbarbell.screens.weighthistory;

import com.headmostlab.quickbarbell.BasePresenter;
import com.headmostlab.quickbarbell.BaseView;
import com.headmostlab.quickbarbell.model.database.entities.WeightHistory;

import java.util.List;

import androidx.lifecycle.LifecycleOwner;

public interface WeightHistoryContract {
    interface View extends BaseView<Presenter> {
        LifecycleOwner getLifeCycleOwner();

        void showTitle(String title);

        void showHistory(List<WeightHistory> weightHistories);

        void showActionMode();

        void hideActionMode();

        void selectAll();

        List<WeightHistory> getSelectedCards();

        void enableActionModeSelectAllItem(boolean enable);

        void enableActionModeDeleteItem(boolean enable);

        void clearSelection();
    }

    interface Presenter extends BasePresenter<View> {
        void restoreState(String xml);

        String serialize();

        void selectAll();

        void deleteHistory();

        void selectCards(List<WeightHistory> selectedCards);
    }
}
