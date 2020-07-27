package com.headmostlab.quickbarbell.model.database.dao;

import com.headmostlab.quickbarbell.model.database.entities.Bunch;

import java.math.BigDecimal;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public abstract class BunchDao {

    @Query("SELECT * FROM Bunch WHERE weight between :minWeight and :maxWeight")
    public abstract List<Bunch> getByWeight(BigDecimal minWeight, BigDecimal maxWeight);

    @Query("SELECT * FROM Bunch")
    public abstract List<Bunch> getAll();

    @Insert
    protected abstract long _insert(Bunch bunch);

    public void insert(Bunch bunch) {
        bunch.setBunchId(_insert(bunch));
    }

    @Insert
    public abstract void insertAll(Bunch... bunches);

    @Query("DELETE FROM Bunch")
    public abstract void deleteAll();
}
