package com.headmostlab.quickbarbell.business;

import com.headmostlab.quickbarbell.model.database.dao.DiskDao;
import com.headmostlab.quickbarbell.model.database.entities.Disk;
import com.headmostlab.quickbarbell.utils.DiskUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class RealTimeCalculator {

    private Map<BigDecimal, List<Disk>> disksGroupedByWeight;
    private Map<BigDecimal, List<Disk>> halfDisksGroupedByWeight;
    private Map<BigDecimal, List<Disk>> quarterDisksGroupedByWeight;

    @Inject
    public RealTimeCalculator(DiskDao diskDao) {
        List<Disk> allDisks = diskDao.getAll();
        List<Disk> halfDisks = DiskUtils.getHalfDisks(allDisks);
        List<Disk> quarterDisks = DiskUtils.getHalfDisks(halfDisks);

        disksGroupedByWeight = DiskUtils.groupDisksByWeight(allDisks, Collections.reverseOrder());
        halfDisksGroupedByWeight = DiskUtils.groupDisksByWeight(halfDisks, Collections.reverseOrder());
        quarterDisksGroupedByWeight = DiskUtils.groupDisksByWeight(quarterDisks, Collections.reverseOrder());
    }

    public List<Disk> findHalfBalancedDiskSet(BigDecimal halfWeightInKg) {
        return findWeight(halfWeightInKg, halfDisksGroupedByWeight);
    }

    public List<Disk> findQuarterBalancedDiskSet(BigDecimal halfWeightInKg) {
        return findWeight(halfWeightInKg, quarterDisksGroupedByWeight);
    }

    public List<Disk> findDisbalancedDiskSet(BigDecimal weightInKg) {
        return findWeight(weightInKg, disksGroupedByWeight);
    }

    private List<Disk> findWeight(BigDecimal weightInKg, Map<BigDecimal, List<Disk>> diskByWeight) {
        List<Disk> result = new ArrayList<>();
        BigDecimal target = weightInKg;
        for (Map.Entry<BigDecimal, List<Disk>> entry : diskByWeight.entrySet()) {
            for (Disk disk : entry.getValue()) {
                if (target.compareTo(entry.getKey()) >= 0) {
                    result.add(disk);
                    target = target.subtract(entry.getKey());
                }
            }
        }
        return result;
    }

}
