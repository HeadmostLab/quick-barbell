package com.headmostlab.quickbarbell.model.dbfillers;

import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.model.database.dao.DiskDao;
import com.headmostlab.quickbarbell.model.database.dao.WeightTemplateDao;

import javax.inject.Inject;

public class KgDbFiller extends DbFiller {

    private static final String[] DISK_WEIGHTS = {"1.25", "2.5", "5", "10", "15", "20"};
    private static final String[] BAR_WEIGHTS = {"6.5", "9", "2.3", "2.3"};
    private static final String[][] TEMPLATE_WEIGHTS = {{"52", "94", "37"}, {"79", "87"}, {"33"}, {"22", "19"}};

    @Inject
    public KgDbFiller(DiskDao diskDao, BarDao barDao, WeightTemplateDao templateDao) {
        super(diskDao, barDao, templateDao);
    }

    @Override
    public void fill() {
        fill(DISK_WEIGHTS, BAR_WEIGHTS, TEMPLATE_WEIGHTS, MeasurementUnit.KILOGRAM);
    }
}
