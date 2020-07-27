package com.headmostlab.quickbarbell.views.recyclerview.weightslist.model;

public class DiskCardHidden extends DiskCard {

    public DiskCardHidden(DiskCard diskCard) {
        super(diskCard.weight, diskCard.getCount(), diskCard.getUnit(), diskCard.isShowUnit);
    }

    @Override
    public String toString() {
        return super.toString().replaceAll(".?", String.valueOf((char)45));
    }
}
