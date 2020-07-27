package com.headmostlab.quickbarbell.model.database.dao;

import com.headmostlab.quickbarbell.model.database.entities.WeightTemplate;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public abstract class WeightTemplateDao {

    @Query("SELECT * FROM weighttemplate")
    public abstract List<WeightTemplate> getAll();

    @Query("SELECT * FROM weighttemplate WHERE id = :templateId")
    public abstract WeightTemplate getById(Long templateId);

    @Query("SELECT * FROM weighttemplate WHERE barid = :barId")
    public abstract LiveData<List<WeightTemplate>> getByBarId(long barId);

    @Insert
    protected abstract long _insert(WeightTemplate weightTemplate);

    public void insert(WeightTemplate weightTemplate) {
        weightTemplate.setId(_insert(weightTemplate));
    }

    @Update
    public abstract void update(WeightTemplate weightTemplate);

    @Update
    public abstract void update(List<WeightTemplate> weightTemplates);

    @Delete
    public abstract void delete(WeightTemplate weightTemplate);

    @Query("DELETE FROM weighttemplate")
    public abstract void delete();

    @Delete
    public abstract void delete(List<WeightTemplate> templates);

}
