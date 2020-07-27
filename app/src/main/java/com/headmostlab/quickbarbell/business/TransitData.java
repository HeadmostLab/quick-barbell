package com.headmostlab.quickbarbell.business;

import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;
import com.headmostlab.quickbarbell.di.ApplicationScope;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.model.database.entities.WeightTemplate;

import java.math.BigDecimal;

import javax.inject.Inject;

@ApplicationScope
public class TransitData {

    private Bar chosenBar;
    private BigDecimal preciseWeight;
    private boolean balanced;
    private AssembledBar assembledBar;
    private WeightTemplate selectedTemplate;

    @Inject
    public TransitData() {
    }

    public void setChosenBar(Bar chosenBar) {
        this.chosenBar = chosenBar;
    }

    public Bar getChosenBar() {
        return chosenBar;
    }

    public void setPreciseWeight(BigDecimal weight) {
        this.preciseWeight = weight;
    }

    public BigDecimal getPreciseWeight() {
        return preciseWeight;
    }

    public boolean isBalanced() {
        return balanced;
    }

    public void setBalanced(boolean balanced) {
        this.balanced = balanced;
    }

    public void setAssembledBar(AssembledBar assembledBar) {
        this.assembledBar = assembledBar;
    }

    public AssembledBar getAssembledBar() {
        return assembledBar;
    }

    public WeightTemplate getSelectedTemplate() {
        return selectedTemplate;
    }

    public void setSelectedTemplate(WeightTemplate selectedTemplate) {
        this.selectedTemplate = selectedTemplate;
    }
}