package com.headmostlab.quickbarbell.model.database.dao;

import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.entities.Disk;

import java.math.BigDecimal;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public abstract class DiskDao {

    //// SELECT

    @Query("SELECT count(1) FROM (SELECT 1 FROM disk GROUP BY unit)")
    public abstract int unitCount();

    public boolean isOneUnit() {
        return unitCount() <= 1;
    }

    @Query("SELECT * FROM disk WHERE hidden = 0")
    public abstract List<Disk> getAll();

    @Query("SELECT * FROM disk WHERE hidden = 0")
    public abstract LiveData<List<Disk>> getAllLive();

    @Query("SELECT * FROM disk WHERE diskId in (:ids) and hidden = 0")
    public abstract List<Disk> getByIds(long[] ids);

    @Query("SELECT COUNT(diskId) FROM disk WHERE hidden = 0")
    public abstract int count();

    @Query("SELECT COUNT(diskId) FROM disk WHERE hidden = 0 LIMIT 1")
    public abstract int hasOne();

    //// INSERT

    @Insert
    protected abstract long _insert(Disk disk);

    public void insert(Disk disk) {
        disk.setDiskId(_insert(disk));
    }

    @Insert
    protected abstract long[] _insertAll(List<Disk> disks);

    public void insertAll(List<Disk> disks) {
        final long[] ids = _insertAll(disks);
        for (int i = 0; i < ids.length; i++) {
            disks.get(i).setDiskId(ids[i]);
        }
    }


    //// UPDATE

    @Query("UPDATE Disk SET hidden = 1")
    public abstract int hide();

    @Query("UPDATE Disk SET hidden = 1 WHERE diskId in (:diskIds)")
    public abstract int hide(long[] diskIds);

    public void hide(List<Disk> disks) {
        hide(getIds(disks));
    }

    private static long[] getIds (List<Disk> disks) {
        long[] diskIds = new long[disks.size()];
        for (int i = 0; i < disks.size(); i++) {
            diskIds[i] = disks.get(i).getDiskId();
        }
        return diskIds;
    }

    @Query("UPDATE Disk SET hidden = 1 WHERE weight = :weight and unit = :unit")
    public abstract void hide(BigDecimal weight, MeasurementUnit unit);


    //// DELETE

    @Query("DELETE FROM Disk WHERE hidden = 1")
    public abstract int deleteHidden();

    @Query("DELETE FROM Disk WHERE weight = :weight and unit = :unit")
    public abstract void delete(BigDecimal weight, MeasurementUnit unit);

    @Query("DELETE FROM Disk WHERE diskId in (:diskIds)")
    public abstract int delete(long[] diskIds);

    public void delete(List<Disk> disks) {
        delete(getIds(disks));
    }

}
