package com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards;

import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.utils.BigDecimalUtils;

public class SelectBarCard extends Card {
    private Bar bar;
    private int icon;
    boolean isShowUnit;

    public SelectBarCard(Bar bar, int icon, boolean isShowUnit) {
        this.bar = bar;
        this.icon = icon;
        this.isShowUnit = isShowUnit;
    }

    public int getIcon() {
        return icon;
    }

    public Bar getBar() {
        return bar;
    }

    public String getWeightString() {
        return isShowUnit ? bar.getWeightWithUnit() : BigDecimalUtils.toString(bar.getWeight());
    }
}
