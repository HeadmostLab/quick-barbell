package com.headmostlab.quickbarbell.business.assembledbar;

import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.business.RealTimeCalculator;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.dao.BunchWithDisksDao;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.model.database.entities.BunchWithDisks;
import com.headmostlab.quickbarbell.model.database.entities.Disk;
import com.headmostlab.quickbarbell.utils.AssembledBarUtils;
import com.headmostlab.quickbarbell.utils.DiskUtils;
import com.headmostlab.quickbarbell.utils.WeightUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

public class AssembledBarsProvider {

    private static final BigDecimal WEIGHT_ERROR_IN_KG = BigDecimal.valueOf(5);
    private static final int MAX_CARD_COUNT = 7;
    private final MeasurementUnit systemUnit;
    private final RealTimeCalculator realTimeCalculator;

    private Bar bar;
    private BigDecimal preciseWeight;
    private final BunchWithDisksDao bunchDao;
    private boolean balanced;
    private int maxCardCount = MAX_CARD_COUNT;
    private int bunchLimit = MAX_CARD_COUNT;

    @Inject
    public AssembledBarsProvider(BunchWithDisksDao bunchDao, MeasurementUnit systemUnit, RealTimeCalculator realTimeCalculator) {
        this.bunchDao = bunchDao;
        this.systemUnit = systemUnit;
        this.realTimeCalculator = realTimeCalculator;
    }

    public void setPreciseWeight(BigDecimal weight) {
        preciseWeight = weight;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public void setBalanced(boolean balanced) {
        this.balanced = balanced;
    }

    public void setAssembledBarCount(int maxCount) {
        this.bunchLimit = maxCount;
        this.maxCardCount = maxCount;
    }

    public List<AssembledBar> getBars() {
        List<AssembledBar> assembledBars = (bar.getBarType() != BarTypes.DOUBLE_DUMBBELL) ?
                (balanced ? getBars(true) : getBars(false)) : getDDBars();

        return assembledBars.subList(0, Math.min(assembledBars.size(), maxCardCount));
    }

    private List<AssembledBar> getBars(boolean balanced) {
        BigDecimal preciseWeightInKg = getPreciseWeightInKg();
        BigDecimal barWeightInKg = bar.getWeightInKg();
        if (bar.getBarType() == BarTypes.DOUBLE_DUMBBELL) {
            preciseWeightInKg = preciseWeightInKg.multiply(BigDecimal.valueOf(2));
            barWeightInKg = barWeightInKg.multiply(BigDecimal.valueOf(2));
        }
        BigDecimal disksWeight = preciseWeightInKg.subtract(barWeightInKg);
        List<AssembledBar> assembledBars = new ArrayList<>();
        createAssembledBarsFromBunches(balanced, disksWeight, assembledBars);
        createAssembledBarsFromRealTimeCalculator(balanced, disksWeight, assembledBars);
        Collections.sort(assembledBars, getAssembledBarComparator(preciseWeightInKg));
        return assembledBars;
    }

    private void createAssembledBarsFromBunches(boolean balanced, BigDecimal disksWeight, List<AssembledBar> assembledBars) {
        BigDecimal minDiskWeightInKg = disksWeight.subtract(WEIGHT_ERROR_IN_KG).max(BigDecimal.ZERO);
        BigDecimal maxDiskWeightInKg = disksWeight.add(WEIGHT_ERROR_IN_KG).max(BigDecimal.ZERO);

        final List<BunchWithDisks> bunches = balanced ?
                bunchDao.getBalancedByWeight(disksWeight, minDiskWeightInKg, maxDiskWeightInKg, bunchLimit) :
                bunchDao.getByWeight(disksWeight, minDiskWeightInKg, maxDiskWeightInKg, bunchLimit);

        for (BunchWithDisks bunch : bunches) {
            if (!balanced && bunch.disks.size() > 1 || bunch.bunch.isBalanced()) {
                DiskUtils.sort(bunch.disks);

                LinkedList<Disk> left = new LinkedList<>();
                LinkedList<Disk> right = new LinkedList<>();
                distributeDisks(bunch.disks, left, right);

                assembledBars.add(new AssembledBar(bar, left, right));
            }
        }
    }

    private void createAssembledBarsFromRealTimeCalculator(boolean balanced, BigDecimal disksWeight, List<AssembledBar> assembledBars) {

        BigDecimal minDiskWeightInKg = disksWeight.subtract(WEIGHT_ERROR_IN_KG).max(BigDecimal.ZERO);
        BigDecimal maxDiskWeightInKg = disksWeight.add(WEIGHT_ERROR_IN_KG).max(BigDecimal.ZERO);

        for (BigDecimal i = WEIGHT_ERROR_IN_KG.negate(); i.compareTo(WEIGHT_ERROR_IN_KG) <= 0; i = i.add(new BigDecimal("0.1"))) {

            final BigDecimal targetDisksWeight = disksWeight.add(i);
            if (targetDisksWeight.signum() <= 0) {
                continue;
            }

            {
                final List<Disk> disks = realTimeCalculator.findHalfBalancedDiskSet(targetDisksWeight.multiply(new BigDecimal("0.5")));
                DiskUtils.sort(disks);
                addAssembledBar(assembledBars, minDiskWeightInKg, maxDiskWeightInKg, disks, disks);
            }

            {
                final List<Disk> disks = realTimeCalculator.findQuarterBalancedDiskSet(targetDisksWeight.multiply(new BigDecimal("0.25")));
                disks.addAll(disks);
                DiskUtils.sort(disks);
                addAssembledBar(assembledBars, minDiskWeightInKg, maxDiskWeightInKg, disks, disks);
            }

            if (!balanced) {
                LinkedList<Disk> left = new LinkedList<>();
                LinkedList<Disk> right = new LinkedList<>();
                final List<Disk> disks = realTimeCalculator.findDisbalancedDiskSet(targetDisksWeight);
                DiskUtils.sort(disks);
                distributeDisks(disks, left, right);
                addAssembledBar(assembledBars, minDiskWeightInKg, maxDiskWeightInKg, left, right);
            }
        }
    }

    private void addAssembledBar(List<AssembledBar> assembledBars, BigDecimal minDiskWeightInKg, BigDecimal maxDiskWeightInKg, List<Disk> left, List<Disk> right) {
        if (!left.isEmpty() && !right.isEmpty()) {
            final BigDecimal foundDisksWeight = DiskUtils.getWeightInKg(left, right);
            if (foundDisksWeight.compareTo(minDiskWeightInKg) >= 0 && foundDisksWeight.compareTo(maxDiskWeightInKg) <= 0) {
                AssembledBar assembledBar = new AssembledBar(bar, left, right);
                if (!AssembledBarUtils.findSame(assembledBars, assembledBar)) {
                    assembledBars.add(assembledBar);
                }
            }
        }
    }

    private List<AssembledBar> getDDBars() {
        final List<AssembledBar> tmpAssembledBars = getBars(true);
        List<AssembledBar> assembledBars = new ArrayList<>();
        for (AssembledBar assembledBar : tmpAssembledBars) {
            LinkedList<Disk> left = new LinkedList<>();
            LinkedList<Disk> right = new LinkedList<>();
            distributeDisks(assembledBar.getLeftDisks(), left, right);
            if (!left.isEmpty() && !right.isEmpty()) {
                assembledBars.add(new AssembledBar(bar, left, right));
            }
        }

        Collections.sort(assembledBars, getAssembledBarComparator(getPreciseWeightInKg()));

        return assembledBars;
    }

    private void distributeDisks(List<Disk> disks, LinkedList<Disk> left, LinkedList<Disk> right) {
        float leftWeightInKg = 0;
        float rightWeightInKg = 0;
        while (!disks.isEmpty()) {
            final Disk disk = disks.remove(disks.size() - 1);
            if (leftWeightInKg < rightWeightInKg) {
                left.addFirst(disk);
                leftWeightInKg += disk.getWeightInKg().floatValue();
            } else {
                right.addFirst(disk);
                rightWeightInKg += disk.getWeightInKg().floatValue();
            }
        }
    }

    private Comparator<AssembledBar> getAssembledBarComparator(BigDecimal preciseWeightInKg) {
        return (b1, b2) -> {
            BigDecimal w1 = b1.getWeightInKg().subtract(preciseWeightInKg).abs();
            BigDecimal w2 = b2.getWeightInKg().subtract(preciseWeightInKg).abs();
            if (w1.compareTo(w2) != 0) {
                return w1.compareTo(w2);
            }
            int c1 = b1.getLeftDisks().size() + b1.getRightDisks().size();
            int c2 = b2.getLeftDisks().size() + b2.getRightDisks().size();
            return Integer.compare(c1, c2);
        };
    }

    private BigDecimal getPreciseWeightInKg() {
        return WeightUtils.convert(preciseWeight, systemUnit, MeasurementUnit.KILOGRAM);
    }
}