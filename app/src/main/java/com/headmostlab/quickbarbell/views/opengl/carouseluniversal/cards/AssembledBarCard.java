package com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards;

import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;

import java.math.BigDecimal;

public class AssembledBarCard extends Card {
    private AssembledBar assembledBar;
    private BigDecimal weight;
    private BigDecimal weightOffset;

    public AssembledBarCard(AssembledBar assembledBar, BigDecimal weight, BigDecimal weightOffset) {
        this.assembledBar = assembledBar;
        this.weight = weight;
        this.weightOffset = weightOffset;
    }

    public AssembledBar getAssembledBar() {
        return assembledBar;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public BigDecimal getWeightOffset() {
        return weightOffset;
    }
}
