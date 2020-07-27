package com.headmostlab.quickbarbell.views.recyclerview.weightslist;

import com.headmostlab.quickbarbell.BasePresenter;
import com.headmostlab.quickbarbell.BaseView;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.Card;

import java.util.List;

public interface WeightsListContract {
    interface View extends BaseView<Presenter> {
        void showWeights(List<Card> cards);

        void setPresenter(Presenter presenter);

        void selectAll();

        void enterInSelectionMode();

        void exitSelectionMode();

        void scrollToLast();

        void scrollTo(int position);

        void syncCard(Card card);
    }

    interface Presenter extends BasePresenter<View> {
        void setCards(List<Card> cards);

        void selectCards(List<Card> selectedCards);

        void selectAll();

        List<Card> getSelectedCards();

        void enterInSelectionMode();

        void exitSelectionMode();

        void scrollToLast();

        void scrollTo(int position);

        void update(Card card);
    }
}