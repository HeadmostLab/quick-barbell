package com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards;

import com.headmostlab.quickbarbell.business.BarTypes;

public class InventoryBarCard extends SimpleCard {
    private BarTypes barType;

    public InventoryBarCard(BarTypes barType, int icon) {
        super(icon);
        this.barType = barType;
    }

    public BarTypes getBarType() {
        return barType;
    }

    public void setBarType(BarTypes barType) {
        this.barType = barType;
    }
}
