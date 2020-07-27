package com.headmostlab.quickbarbell.model.database.dao;

import com.headmostlab.quickbarbell.model.database.entities.WeightHistory;
import com.headmostlab.quickbarbell.model.database.entities.WeightTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public abstract class WeightHistoryDao {
    @Query("SELECT * FROM WeightHistory WHERE templateId = :templateId ORDER BY date desc")
    public abstract LiveData<List<WeightHistory>> getById(long templateId);

    @Query("SELECT * FROM WeightHistory WHERE templateId = :templateId ORDER BY date desc LIMIT 1")
    public abstract WeightHistory getLast(long templateId);

    @Insert
    protected abstract long _insert(WeightHistory weightHistory);

    public void insert(WeightHistory weightHistory) {
        weightHistory.setId(_insert(weightHistory));
    }

    public WeightHistory insert(WeightTemplate template, Date date) {
        WeightHistory weightHistory = new WeightHistory(template.getId(),
                date, template.getWeight(), template.getUnit());
        weightHistory.setId(_insert(weightHistory));
        return weightHistory;
    }

    public WeightHistory insert(WeightTemplate template) {
        return insert(template, Calendar.getInstance().getTime());
    }

    @Insert
    protected abstract long[] _insertAll(List<WeightHistory> histories);

    public void insertAll(List<WeightHistory> histories) {
        final long[] ids = _insertAll(histories);
        for (int i = 0; i < ids.length; i++) {
            histories.get(i).setId(ids[i]);
        }
    }

    @Delete
    public abstract void deleteAll(List<WeightHistory> currentHistories);
}
