package com.headmostlab.quickbarbell.business.assembledbar;

import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.model.database.entities.Disk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.room.Ignore;

public class AssembledBar {
    private Bar bar;
    private List<Disk> leftDisks;
    private List<Disk> rightDisks;

    @Inject
    public AssembledBar() {
        leftDisks = new ArrayList<>();
        rightDisks = new ArrayList<>();
    }

    @Ignore
    public AssembledBar(Bar bar, List<Disk> leftDisks, List<Disk> rightDisks) {
        this.bar = bar;
        this.leftDisks = leftDisks;
        this.rightDisks = rightDisks;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public List<Disk> getLeftDisks() {
        return leftDisks;
    }

    public void setLeftDisks(List<Disk> leftDisks) {
        this.leftDisks = leftDisks;
    }

    public List<Disk> getRightDisks() {
        return rightDisks;
    }

    public void setRightDisks(List<Disk> rightDisks) {
        this.rightDisks = rightDisks;
    }

    public BigDecimal getWeightInKg() {
        BigDecimal weight = BigDecimal.ZERO;

        if (bar != null) {
            weight = weight.add(bar.getWeightInKg());
        }

        if (leftDisks != null) {
            for (Disk disk : leftDisks) {
                weight = weight.add(disk.getWeightInKg());
            }
        }

        if (rightDisks != null) {
            for (Disk disk : rightDisks) {
                weight = weight.add(disk.getWeightInKg());
            }
        }

        return weight;
    }
}
