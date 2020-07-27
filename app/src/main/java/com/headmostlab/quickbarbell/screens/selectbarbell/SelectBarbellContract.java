package com.headmostlab.quickbarbell.screens.selectbarbell;

import com.headmostlab.quickbarbell.BasePresenter;
import com.headmostlab.quickbarbell.BaseView;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.SelectBarCard;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public interface SelectBarbellContract {

    interface View extends BaseView<Presenter> {
        void showSelectWeightScreen();
        void showCards(@NonNull List<SelectBarCard> cards);
        void hideHelpIcon(boolean isHidden);
        void showShopButton(boolean isShow);
        void showSettingsScreen();
        void showHelpScreen();
        LifecycleOwner getLifeCycleOwner();
        void goToBar(Bar bar);
        void scrollScrollBarTo(SelectBarCard card);
        void scrollCarouselViewTo(SelectBarCard card);
    }

    interface Presenter extends BasePresenter<View>, LifecycleObserver {
        void selectCardByCarouselView(SelectBarCard card);
        void selectCardByScrollBar(SelectBarCard card);
        void clickBarCard();
        void loadCards();
        void restoreState(String xml);
        String serialize();
    }

}
