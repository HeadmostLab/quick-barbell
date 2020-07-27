package com.headmostlab.quickbarbell.model.database.dao;

import com.headmostlab.quickbarbell.model.database.entities.Bar;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public abstract class BarDao {

    @Query("SELECT count(1) FROM (SELECT 1 FROM bar GROUP BY unit)")
    public abstract int unitCount();

    public boolean isOneUnit() {
        return unitCount() <= 1;
    }

    @Query("SELECT COUNT(id) FROM bar LIMIT 1")
    public abstract int hasOne();

    @Query("SELECT * FROM bar")
    public abstract LiveData<List<Bar>> getAll();

    @Query("SELECT * FROM bar WHERE id = :id")
    public abstract Bar getById(long id);

    @Query("SELECT * FROM bar")
    public abstract List<Bar> getAllSimple();

    @Insert
    protected abstract long _insert(Bar bar);

    public void insert(Bar bar) {
        bar.setId(_insert(bar));
    }

    @Delete
    public abstract int deleteAll(List<Bar> bars);

    @Query("DELETE FROM bar")
    public abstract int deleteAll();

    @Update
    public abstract void update(Bar bar);

    @Update
    public abstract void update(List<Bar> bars);
}
