package com.headmostlab.quickbarbell.screens.selectassembledbar;

import com.headmostlab.quickbarbell.BasePresenter;
import com.headmostlab.quickbarbell.BaseView;
import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.AssembledBarCard;

import java.util.List;

public interface SelectAssembledBarContract {
    interface View extends BaseView<Presenter> {
        void showTitle(String title);
        void showShowWeightsScreen();
        void scrollScrollBarTo(AssembledBarCard card);
        void scrollCarouselViewTo(AssembledBarCard card);
        void showCards(List<AssembledBarCard> cards);
    }

    interface Presenter extends BasePresenter<View> {
        void selectCardByCarouselView(AssembledBarCard card);
        void selectCardByScrollBar(AssembledBarCard card);
        void clickCurCard();
        BarTypes getBarType();
        MeasurementUnit getMeasurement();
        void restoreState(String xml);
        String serialize();
    }
}
