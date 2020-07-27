package com.headmostlab.quickbarbell.model.dbfillers;

import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.model.database.dao.DiskDao;
import com.headmostlab.quickbarbell.model.database.dao.WeightTemplateDao;

import javax.inject.Inject;

public class LbDbFiller extends DbFiller {

    private static final String[] DISK_WEIGHTS = {"2.5", "5", "10", "25", "35", "45"};
    private static final String[] BAR_WEIGHTS = {"14", "20", "5", "5"};
    private static final String[][] TEMPLATE_WEIGHTS = {{"114", "207", "81"}, {"174", "192"}, {"73"}, {"48", "43"}};

    @Inject
    public LbDbFiller(DiskDao diskDao, BarDao barDao, WeightTemplateDao templateDao) {
        super(diskDao, barDao, templateDao);
    }

    @Override
    public void fill() {
        fill(DISK_WEIGHTS, BAR_WEIGHTS, TEMPLATE_WEIGHTS, MeasurementUnit.POUND);
    }
}
