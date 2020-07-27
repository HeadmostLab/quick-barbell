package com.headmostlab.quickbarbell.model.database.entities;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

public class BunchWithDisks {
    @Embedded public Bunch bunch;
    @Relation(
            parentColumn = "bunchId",
            entityColumn = "diskId",
            associateBy = @Junction(BunchDiskLink.class)
    )
    public List<Disk> disks;
}
