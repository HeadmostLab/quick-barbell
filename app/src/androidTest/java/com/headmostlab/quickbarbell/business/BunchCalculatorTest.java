package com.headmostlab.quickbarbell.business;

import com.headmostlab.quickbarbell.TestApp;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.AppDatabase;
import com.headmostlab.quickbarbell.model.database.dao.DiskDao;
import com.headmostlab.quickbarbell.model.database.entities.BunchWithDisks;
import com.headmostlab.quickbarbell.model.database.entities.Disk;
import com.headmostlab.quickbarbell.model.testutils.TestWeightTableSet;
import com.headmostlab.quickbarbell.testutils.TestUtils;
import com.headmostlab.quickbarbell.utils.DiskUtils;

import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsArrayContainingInAnyOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class BunchCalculatorTest {

    @Inject
    AppDatabase db;

    @Inject
    BunchCalculator bunchCalculator;

    @Before
    public void setUp() throws InterruptedException {
        TestApp application = ApplicationProvider.getApplicationContext();
        application.getAppInjector().inject(this);

        db.clearAllTables();

        fillDatabase();

        bunchCalculator.calcWeights();

        while (bunchCalculator.getCalcStatus() != BunchCalculator.CalcStatus.FINISHED) {
            Thread.sleep(50);
        }
    }

    @Test
    public void testWeightTableGeneration2() {
        MatcherAssert.assertThat(TestWeightTableSet.STRING_SET,
                IsArrayContainingInAnyOrder.arrayContainingInAnyOrder(weightTablesToStingArray(db.bunchWithDisksDao().getAll())));
    }

    private void fillDatabase() {
        fillDisksInDatabase();
    }

    private void fillDisksInDatabase() {
        final DiskDao diskDao = db.diskDao();
        float[] weights = new float[]{1.25f, 1.25f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f,
                5.0f, 5.0f, 15.0f, 15.0f, 15.0f, 15.0f, 20.0f, 20.0f};
        diskDao.insertAll(TestUtils.genDisks(MeasurementUnit.KILOGRAM, weights));
    }

    private String toString(BunchWithDisks bunch) {
        StringBuilder sb = new StringBuilder();
        DiskUtils.sort(bunch.disks);
        for (Disk disk : bunch.disks) {
            sb.append(disk.getWeight()).append(",");
        }
        return sb.toString();
    }

    private String[] weightTablesToStingArray(List<BunchWithDisks> bunches) {

        List<String> list = new ArrayList<>();
        for (BunchWithDisks bunch : bunches) {
            list.add(toString(bunch));
        }
        return list.toArray(new String[0]);
    }

}