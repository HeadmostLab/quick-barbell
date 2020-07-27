package com.headmostlab.quickbarbell.views.recyclerview.weightslist.model;

import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.entities.Bar;

import java.math.BigDecimal;

public class BarCard extends Card {
    private BarTypes barType;
    private Bar bar;

    public BarCard(Bar bar) {
        super(bar.getWeight(), bar.getUnit(), true);
        this.bar = bar;
        this.barType = bar.getBarType();
    }

    public BarCard(BarTypes barType, BigDecimal weight, MeasurementUnit unit, boolean isShowUnit) {
        super(weight, unit, isShowUnit);
        this.barType = barType;
    }

    public BarTypes getBarType() {
        return barType;
    }

    public void setBarType(BarTypes barType) {
        this.barType = barType;
    }

    public Bar getBar() {
        return bar;
    }
}
