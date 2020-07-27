package com.headmostlab.quickbarbell.screens.barinventory;

import com.headmostlab.quickbarbell.BasePresenter;
import com.headmostlab.quickbarbell.BaseView;
import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.InventoryBarCard;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.WeightsListContract;

import java.math.BigDecimal;
import java.util.List;

import androidx.lifecycle.LifecycleOwner;

public interface BarInventoryContract {
    interface View extends BaseView<Presenter> {
        void scrollScrollBarTo(InventoryBarCard card);

        void scrollCarouselViewTo(InventoryBarCard card);

        void showCarouselCards(List<InventoryBarCard> cards);

        void shoWeight(BigDecimal weight);

        void showUnit(MeasurementUnit unit);

        LifecycleOwner getLifeCycleOwner();

        void showActionMode();

        void hideActionMode();

        void enableActionModeSelectAllItem(boolean enable);

        void showMessage(int resourceId);

        void showShop();

        void showEditBarDialog(MeasurementUnit unit, BigDecimal weight, BarTypes barType);

        void showActionModeChangeItem(boolean hide);
    }

    interface Presenter extends BasePresenter<View> {
        void selectCardByCarouselView(InventoryBarCard card);

        void selectCardByScrollBar(InventoryBarCard card);

        void restoreState(String xml);

        String serialize();

        void setWeight(BigDecimal weight);

        WeightsListContract.Presenter getWeightsListPresenter();

        void deleteSelectedBars();

        void onSelectionCardsChanged();

        void createBar();

        void setUnit(MeasurementUnit unit);

        void startToUpdateBar();

        void updateBar(MeasurementUnit unit, BigDecimal weight, BarTypes barType);
    }
}
