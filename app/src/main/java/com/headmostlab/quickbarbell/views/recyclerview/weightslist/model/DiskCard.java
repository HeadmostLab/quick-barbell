package com.headmostlab.quickbarbell.views.recyclerview.weightslist.model;

import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.entities.Disk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DiskCard extends Card {
    private int count;
    private List<Disk> disks;

    public DiskCard(BigDecimal weight, int count, MeasurementUnit unit, boolean isShowUnit) {
        super(weight, unit, isShowUnit);
        this.count = count;
    }

    public DiskCard(Disk disk) {
        super(disk.getWeight(), disk.getUnit(), true);
        disks = new ArrayList<>();
        disks.add(disk);
        count++;
    }

    public void add(Disk disk) {
        disks.add(disk);
        count++;
    }

    public List<Disk> getDisks() {
        return disks;
    }

    public int getCount() {
        return count;
    }

    public String toString() {
        return super.toString()+" ("+count+")";
    }
}
