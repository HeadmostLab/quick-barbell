package com.headmostlab.quickbarbell.model.database.entities;

import java.math.BigDecimal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Bunch {

    @PrimaryKey(autoGenerate = true)
    private long bunchId;

    @NonNull
    @ColumnInfo(index = true)
    private BigDecimal weight;

    @Nullable
    private int count;

    private boolean balanced;

    public Bunch(@NonNull BigDecimal weight, int count, boolean balanced) {
        this.weight = weight;
        this.count = count;
        this.balanced = balanced;
    }

    public long getBunchId() {
        return bunchId;
    }

    public void setBunchId(long bunchId) {
        this.bunchId = bunchId;
    }

    @NonNull
    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(@NonNull BigDecimal weight) {
        this.weight = weight;
    }

    public boolean isBalanced() {
        return balanced;
    }

    public void setBalanced(boolean balanced) {
        this.balanced = balanced;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
