package com.headmostlab.quickbarbell.screens.selectweight;

import com.headmostlab.quickbarbell.BasePresenter;
import com.headmostlab.quickbarbell.BaseView;
import com.headmostlab.quickbarbell.model.database.entities.WeightTemplate;
import com.headmostlab.quickbarbell.views.recyclerview.memoryview.MemoryListContract;

import java.math.BigDecimal;

import androidx.lifecycle.LifecycleOwner;

public interface SelectWeightContract {

    interface View extends BaseView<Presenter> {
        LifecycleOwner getLifeCycleOwner();

        void showWeight(BigDecimal weight);

        void showPercent(int percent);

        void showTemplateCommentDialog(String comment, BigDecimal weight, int percent, boolean balanced, boolean hideBalanced);

        void showSelectAssembledBarScreen();

        void showShowWeightsScreen();

        void showActionMode();

        void hideActionMode();

        void enableActionModeChangeItem(boolean enable);

        void enableActionModeSelectAllItem(boolean enable);

        void enableActionModeDeleteItem(boolean enable);

        void showMessage(int resourceId);

        void hideBalancedCheckBox();

        void checkBalancedCheckBox(boolean on);

        void showWeightHistory();
    }

    interface Presenter extends BasePresenter<View> {
        MemoryListContract.Presenter getMemoryList();

        void setWeightTemplate(WeightTemplate template);

        void setPercent(int percent);

        void setWeight(BigDecimal weight);

        void startToAddOrUpdateTemplate();

        void updateOrAddTemplate(String title, BigDecimal weight, int percent, boolean balanced);

        void updateTemplates();

        void deleteTemplates();

        void findWeights();

        void onSelectionTemplatesChanged();

        BigDecimal getWeight();

        int getPercent();

        void restoreState(String xml);

        String serialize();

        void setBalanced(boolean balanced);

        void showWeightHistoryMode();

        void showHistory(WeightTemplate template);
    }

}
