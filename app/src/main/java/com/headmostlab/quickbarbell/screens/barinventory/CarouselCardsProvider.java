package com.headmostlab.quickbarbell.screens.barinventory;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.InventoryBarCard;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CarouselCardsProvider {

    private List<InventoryBarCard> cards;

    @Inject
    public CarouselCardsProvider() {
        cards = new ArrayList<>();
        addCards();
    }

    private void addCards() {
        cards.add(new InventoryBarCard(BarTypes.STRAIGHT, R.drawable.card_flat_bar_2));
        cards.add(new InventoryBarCard(BarTypes.CURLY, R.drawable.card_ez_bar_2));
        cards.add(new InventoryBarCard(BarTypes.DUMBBELL, R.drawable.card_dumbbell_bar_2));
        cards.add(new InventoryBarCard(BarTypes.DOUBLE_DUMBBELL, R.drawable.card_double_dumbbell_bar_2));
    }

    public List<InventoryBarCard> getCards() {
        return cards;
    }

    public InventoryBarCard find(BarTypes barType) {
        for (InventoryBarCard card : cards) {
            if (card.getBarType().equals(barType)) {
                return card;
            }
        }
        return null;
    }

}
