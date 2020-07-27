package com.headmostlab.quickbarbell.utils;

import com.headmostlab.quickbarbell.model.database.entities.Disk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DiskUtils {
    public static BigDecimal getWeightInKg(List<Disk> leftDisks, List<Disk> rightDisks) {
        BigDecimal w = BigDecimal.ZERO;
        if (leftDisks != null) {
            for (Disk disk : leftDisks) {
                w = w.add(disk.getWeightInKg());
            }
        }
        if (rightDisks != null) {
            for (Disk disk : rightDisks) {
                w = w.add(disk.getWeightInKg());
            }
        }
        return w;
    }

    public static void sort(List<Disk> disks) {
        Collections.sort(disks, (o1, o2) -> o1.getWeightInKg().compareTo(o2.getWeightInKg()));
    }

    public static long[] getDiskIds(List<Disk> disks) {
        long[] ids = new long[disks.size()];
        for (int i = 0; i < disks.size(); i++) {
            ids[i] = disks.get(i).getDiskId();
        }
        return ids;
    }

    public static List<Disk> getDisks(BigDecimal[] weights) {
        List<Disk> disks = new ArrayList<>();
        for (int i = 0; i < weights.length; i++) {
            disks.add(new Disk(weights[i]));
        }
        return disks;
    }

    public static BigDecimal[] getWeights(List<Disk> disks) {
        BigDecimal[] weights = new BigDecimal[disks.size()];
        for (int i = 0; i < disks.size(); i++) {
            weights[i] = disks.get(i).getWeight();
        }
        return weights;
    }

    public static String serializeDisks(List<Disk> disks) {
        StringBuilder sb = new StringBuilder();
        for (Disk disk : disks) {
            sb.append(disk.getDiskId()).append(";");
        }
        return sb.toString();
    }

    public static long[] deserializeDisks(String disks) {
        final String[] diskIds = disks.split(";");
        long[] ids = new long[diskIds.length];
        for (int i = 0; i < diskIds.length; i++) {
            ids[i] = Long.parseLong(diskIds[i]);
        }
        return ids;
    }

    /**
     * Возвращает половину блинов с учетом весов и unit
     */
    public static List<Disk> getHalfDisks(List<Disk> disks) {
        Map<BigDecimal, List<Disk>> disksByWeight = groupDisksByWeight(disks, null);
        List<Disk> disksResult = new ArrayList<>();
        for (Map.Entry<BigDecimal, List<Disk>> entry : disksByWeight.entrySet()) {
            List<Disk> disksWithSameWeight = entry.getValue();
            disksResult.addAll(disksWithSameWeight.subList(0, disksWithSameWeight.size() / 2));
        }
        return disksResult;
    }

    public static Map<BigDecimal, List<Disk>> groupDisksByWeight(List<Disk> disks, Comparator<BigDecimal> comparator) {
        Map<BigDecimal, List<Disk>> disksByWeight = new TreeMap<>(comparator);
        for (Disk disk : disks) {
            List<Disk> disksWithSameWeight = disksByWeight.get(disk.getWeightInKg());
            if (disksWithSameWeight == null) {
                disksWithSameWeight = new ArrayList<>();
                disksByWeight.put(disk.getWeightInKg(), disksWithSameWeight);
            }
            disksWithSameWeight.add(disk);
        }
        return disksByWeight;
    }

    public static boolean isEquals(List<Disk> disks1, List<Disk> disks2) {
        if (disks1.size() != disks2.size()) {
            return false;
        }

        sort(disks1);
        sort(disks2);

        final Iterator<Disk> iterator1 = disks1.iterator();
        final Iterator<Disk> iterator2 = disks2.iterator();

        while (iterator1.hasNext()) {
            if (!iterator1.next().equals(iterator2.next())) {
                return false;
            }
        }

        return true;
    }

    public static boolean isBalanced(List<Disk> disks) {
        if (disks.size() == 0 || disks.size() % 2 != 0) {
            return false;
        }
        for (List<Disk> disksWithSameWeight : DiskUtils.groupDisksByWeight(disks, null).values()) {
            if (disksWithSameWeight.size() % 2 != 0) {
                return false;
            }
        }
        return true;
    }
}
