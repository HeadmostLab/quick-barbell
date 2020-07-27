package com.headmostlab.quickbarbell.model.dbfillers;

import com.headmostlab.quickbarbell.App;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.model.database.dao.DiskDao;
import com.headmostlab.quickbarbell.model.database.dao.WeightTemplateDao;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.model.database.entities.Disk;
import com.headmostlab.quickbarbell.model.database.entities.WeightTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class DbFiller {

    private static final int COUNT_OF_SAME_WEIGHT_DISKS = 20;
    private static final int TEMPLATE_PERCENT = 100;
    private static final BarTypes[] BAR_TYPES = {BarTypes.CURLY, BarTypes.STRAIGHT, BarTypes.DUMBBELL, BarTypes.DOUBLE_DUMBBELL};
    private static final boolean TEMPLATE_BALANCED[][] = {{true, true, true}, {true, true}, {false}, {true, true}};
    private static int[][] TEMPLATE_NAMES = new int[][]{
            {R.string.db_biceps, R.string.db_back, R.string.db_triceps},
            {R.string.db_bench_press, R.string.db_squat},
            {R.string.db_leg_biceps},
            {R.string.db_chest_flyes, R.string.db_shoulders}
    };

    private DiskDao diskDao;
    private BarDao barDao;
    private WeightTemplateDao templateDao;

    public DbFiller(DiskDao diskDao, BarDao barDao, WeightTemplateDao templateDao) {
        this.diskDao = diskDao;
        this.barDao = barDao;
        this.templateDao = templateDao;
    }

    protected void insertDisks(BigDecimal weight, MeasurementUnit unit, int count) {
        List<Disk> disks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            disks.add(new Disk(weight, unit));
        }
        diskDao.insertAll(disks);
    }

    protected Bar insertBar(BigDecimal weight, BarTypes type, MeasurementUnit unit) {
        final Bar bar = new Bar(type, weight, unit);
        barDao.insert(bar);
        return bar;
    }

    protected void insertWeightTemplate(Bar bar, BigDecimal weight, MeasurementUnit unit, String name, boolean balanced) {
        templateDao.insert(new WeightTemplate(bar.getId(), weight, unit, TEMPLATE_PERCENT, name, balanced));
    }

    protected void fill(String[] diskWeights, String[] barWeights, String[][] templateWeight, MeasurementUnit unit) {

        for (String weight : diskWeights) {
            insertDisks(new BigDecimal(weight), unit, COUNT_OF_SAME_WEIGHT_DISKS);
        }

        for (int i = 0; i < BAR_TYPES.length; i++) {
            Bar bar = insertBar(new BigDecimal(barWeights[i]), BAR_TYPES[i], unit);

            for (int j = 0; j < TEMPLATE_BALANCED[i].length; j++) {
                insertWeightTemplate(bar, new BigDecimal(templateWeight[i][j]), unit,
                        App.getInstance().getString(TEMPLATE_NAMES[i][j]), TEMPLATE_BALANCED[i][j]);
            }
        }
    }

    public abstract void fill();
}
