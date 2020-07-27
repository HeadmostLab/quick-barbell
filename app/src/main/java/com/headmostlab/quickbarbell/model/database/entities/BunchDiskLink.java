package com.headmostlab.quickbarbell.model.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

@Entity(
        primaryKeys = {"bunchId", "diskId"},
        foreignKeys = {
        @ForeignKey(entity = Disk.class, parentColumns = "diskId", childColumns = "diskId",
                onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Bunch.class, parentColumns = "bunchId", childColumns = "bunchId",
                onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE)})
public class BunchDiskLink {

    @ColumnInfo(index = true)
    private long bunchId;

    @ColumnInfo(index = true)
    private long diskId;

    public BunchDiskLink() {
    }

    @Ignore
    public BunchDiskLink(long bunchId, long diskId) {
        this.bunchId = bunchId;
        this.diskId = diskId;
    }

    public long getBunchId() {
        return bunchId;
    }

    public void setBunchId(long bunchId) {
        this.bunchId = bunchId;
    }

    public long getDiskId() {
        return diskId;
    }

    public void setDiskId(long diskId) {
        this.diskId = diskId;
    }
}
