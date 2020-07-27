package com.headmostlab.quickbarbell.model.database.dao;

import com.headmostlab.quickbarbell.model.database.entities.BunchWithDisks;

import java.math.BigDecimal;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface BunchWithDisksDao {

    @Transaction
    @Query("SELECT * FROM Bunch WHERE weight BETWEEN :minWeight AND :maxWeight  ORDER BY abs(:targetWeight-weight) LIMIT :limit")
    List<BunchWithDisks> getByWeight(BigDecimal targetWeight, BigDecimal minWeight, BigDecimal maxWeight, int limit);

    @Transaction
    @Query("SELECT * FROM Bunch WHERE balanced = 1 and weight BETWEEN :minWeight AND :maxWeight  ORDER BY abs(:targetWeight-weight) LIMIT :limit")
    List<BunchWithDisks> getBalancedByWeight(BigDecimal targetWeight, BigDecimal minWeight, BigDecimal maxWeight, int limit);

    @Transaction
    @Query("SELECT * FROM Bunch")
    List<BunchWithDisks> getAll();



}
