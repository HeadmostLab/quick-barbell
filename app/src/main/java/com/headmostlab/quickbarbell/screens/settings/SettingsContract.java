package com.headmostlab.quickbarbell.screens.settings;

import com.headmostlab.quickbarbell.BasePresenter;
import com.headmostlab.quickbarbell.BaseView;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.ActivityCard;

import java.util.List;

public interface SettingsContract {

    interface View extends BaseView<Presenter> {
        void showCards(List<ActivityCard> cards);
        void showScreen(ActivityCard card);
        void showTitle(String title);
        void scrollScrollBarTo(ActivityCard card);
        void scrollCarouselViewTo(ActivityCard card);
    }

    interface Presenter extends BasePresenter<View> {
        void restoreState(String xml);
        String serialize();
        void clickOnCard();
        void selectCardByCarouselView(ActivityCard card);
        void selectCardByScrollBar(ActivityCard card);
    }

}
