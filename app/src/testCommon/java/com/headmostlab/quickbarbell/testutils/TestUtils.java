package com.headmostlab.quickbarbell.testutils;

import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;
import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.model.database.entities.Disk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestUtils {

    private static Random random = new Random();
    private static final Sequence intSeq = new Sequence();

    public static List<Disk> genDisks(MeasurementUnit unit, float... weights) {
        List<Disk> disks = new ArrayList<>();
        for (float weight : weights) {
            final Disk disk = new Disk(BigDecimal.valueOf(weight), unit);
            disk.setDiskId(random.nextInt());
            disks.add(disk);
        }
        return disks;
    }

    private static BarTypes nextBarType() {
        final BarTypes[] values = BarTypes.values();
        return values[random.nextInt(values.length)];
    }

    public static MeasurementUnit nextUnit() {
        final MeasurementUnit[] values = MeasurementUnit.values();
        return values[random.nextInt(values.length)];
    }

    public static Bar generateBar(long id, BigDecimal weight, MeasurementUnit unit, BarTypes barType) {
        final Bar bar = new Bar();
        bar.setId(id);
        bar.setWeight(weight);
        bar.setBarType(barType);
        bar.setUnit(unit);
        return bar;
    }

    public static Bar generateBar(BigDecimal weight) {
        return generateBar(intSeq.next(), weight, nextUnit(), nextBarType());
    }

    public static Bar generateBar(BigDecimal weight, MeasurementUnit unit) {
        return generateBar(intSeq.next(), weight, unit, nextBarType());
    }

    public static Bar generateBar() {
        return generateBar(intSeq.next(), WeightGenerator.next(), nextUnit(), nextBarType());
    }

    public static List<Bar> generateBars(int count) {
        List<Bar> bars = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            bars.add(generateBar());
        }
        return bars;
    }

    public static float[] getWeights(List<Disk> disks) {
        float[] weights = new float[disks.size()];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = disks.get(i).getWeight().floatValue();
        }
        return weights;
    }

    public static List<AssembledBar> generateAssembledBars(int count) {
        List<AssembledBar> bars = new ArrayList<>();
        final Bar bar = generateBar();
        final List<Disk> disks = genDisks(MeasurementUnit.KILOGRAM, 1.25f, 2.5f, 5f);
        for (int i = 0; i < count; i++) {
            bars.add(new AssembledBar(bar, disks, disks));
        }
        return bars;
    }
}
