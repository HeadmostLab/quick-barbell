package com.headmostlab.quickbarbell.views.recyclerview.weightslist.model;

public class BarCardHidden extends BarCard {

    public BarCardHidden(BarCard card) {
        super(card.getBarType(), card.weight, card.unit, card.isShowUnit);
    }

    @Override
    public String toString() {
        return super.toString().replaceAll(".?", String.valueOf((char)45));
    }
}
