package com.headmostlab.quickbarbell.model.database.dao;

import com.headmostlab.quickbarbell.model.database.entities.BunchDiskLink;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface BunchDiskLinkDao {

    @Insert
    void insert(BunchDiskLink link);

    @Insert
    void insertAll(List<BunchDiskLink> links);
}
