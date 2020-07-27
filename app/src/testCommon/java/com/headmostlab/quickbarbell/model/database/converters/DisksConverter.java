package com.headmostlab.quickbarbell.model.database.converters;

import com.headmostlab.quickbarbell.di.ApplicationScope;
import com.headmostlab.quickbarbell.model.database.entities.Disk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import androidx.room.TypeConverter;

@ApplicationScope
public class DisksConverter {

    @Inject
    public DisksConverter() {
    }

    // сохраниение дисков с сортировкой по убыванию веса
    @TypeConverter
    public String fromDisks (List<Disk> disks) {
        Map<BigDecimal, Integer> map = new TreeMap<>();
        for (Disk disk : disks) {
            final Integer count = map.get(disk.getWeight());
            map.put(disk.getWeight(), 1 + (count != null ? count : 0));
        }
        StringBuffer s = new StringBuffer();
        for (Map.Entry<BigDecimal, Integer> entry : map.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                s.append(entry.getKey()).append(" ");
            }
        }
        s.deleteCharAt(s.length()-1);
        return s.toString();
    }

    @TypeConverter
    public List<Disk> toDisks(String disksData) {
        List<Disk> disks = new ArrayList<>();
        for (final String diskWeight : disksData.split(" ")) {
            disks.add(new Disk(){{setWeight(new BigDecimal(diskWeight));}});
        }
        return disks;
    }



}
