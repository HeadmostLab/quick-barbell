package com.headmostlab.quickbarbell.views.recyclerview.weightslist;

import com.headmostlab.quickbarbell.model.SettingsModel;
import com.headmostlab.quickbarbell.views.recyclerview.weightslist.model.Card;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class WeightsListPresenter implements WeightsListContract.Presenter {

    private SettingsModel settingsModel;
    private WeakReference<WeightsListContract.View> view;
    private List<Card> cards;
    private List<Card> selectedCards;

    @Inject
    public WeightsListPresenter(SettingsModel settingsModel) {
        this.settingsModel = settingsModel;
        view = new WeakReference<>(null);
        cards = Collections.emptyList();
    }

    @Override
    public void takeView(@NonNull WeightsListContract.View view) {
        this.view = new WeakReference<>(view);
    }

    @Override
    public WeightsListContract.View view() {
        return view.get();
    }

    @Override
    public boolean isView() {
        return view() != null;
    }

    private void showWeights() {
        if (isView()) {
            view().showWeights(cards);
        }
    }

    @Override
    public void setCards(List<Card> cards) {
        this.cards = cards;
        showWeights();
    }

    @Override
    public void selectCards(List<Card> selectedCards) {
        this.selectedCards = selectedCards;
    }

    @Override
    public void selectAll() {
        if (isView()) {
            view().selectAll();
        }
    }

    @Override
    public List<Card> getSelectedCards() {
        return selectedCards;
    }

    @Override
    public void enterInSelectionMode() {
        if (isView()) {
            view().enterInSelectionMode();
        }
    }

    @Override
    public void exitSelectionMode() {
        if (isView()) {
            view().exitSelectionMode();
        }
    }

    @Override
    public void scrollToLast() {
        if (isView()) {
            view().scrollToLast();
        }
    }

    @Override
    public void scrollTo(int position) {
        if (isView()) {
            view().scrollTo(position);
        }
    }

    @Override
    public void update(Card card) {
        if (isView()) {
            view().syncCard(card);
        }
    }
}
