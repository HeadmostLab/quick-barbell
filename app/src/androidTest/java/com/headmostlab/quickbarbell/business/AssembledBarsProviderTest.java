package com.headmostlab.quickbarbell.business;

import com.headmostlab.quickbarbell.TestApp;
import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;
import com.headmostlab.quickbarbell.business.assembledbar.AssembledBarsProvider;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.AppDatabase;
import com.headmostlab.quickbarbell.model.database.converters.DisksConverter;
import com.headmostlab.quickbarbell.model.database.dao.BarDao;
import com.headmostlab.quickbarbell.model.database.dao.DiskDao;
import com.headmostlab.quickbarbell.model.database.entities.Bar;
import com.headmostlab.quickbarbell.testutils.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(AndroidJUnit4.class)
public class AssembledBarsProviderTest {

    public static final String ASSEMBLED_BAR_1 = "total: 139.00; CURLY 6.5; left:1.25 2.5 2.5 2.5 2.5 5 15 15 20; right:1.25 2.5 2.5 2.5 2.5 5 15 15 20";
    public static final String ASSEMBLED_BAR_2 = "total: 68.55; DOUBLE_DUMBBELL 2.3; left:2.5 15 15; right:1.25 2.5 2.5 2.5 5 20";

    @Inject
    AppDatabase db;

    @Inject
    AssembledBarsProvider assembledBarsProvider;

    @Inject
    DisksConverter diskConverter;

    @Inject
    BunchCalculator bunchCalculator;

    @Before
    public void setUp() throws Exception {

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
    public void testAssembledBarSelection() {
        assembledBarsProvider.setBar(createBar(BarTypes.CURLY, new BigDecimal("6.5")));
        assembledBarsProvider.setPreciseWeight(BigDecimal.valueOf(144));
        assertThat(toString(assembledBarsProvider.getBars().get(0)), equalTo(ASSEMBLED_BAR_1));
    }

    @Test
    public void testDoubleDumbbellAssembledBarSelection() {
        assembledBarsProvider.setBar(createBar(BarTypes.DOUBLE_DUMBBELL, new BigDecimal("2.3")));
        assembledBarsProvider.setPreciseWeight(BigDecimal.valueOf(73));
        assertThat(toString(assembledBarsProvider.getBars().get(0)), equalTo(ASSEMBLED_BAR_2));
    }

    private void fillDatabase() {
        fillBarsInDatabase();
        fillDisksInDatabase();
    }

    private void fillBarsInDatabase() {
        final BarDao barDao = db.barDao();
        barDao.insert(createBar(BarTypes.CURLY, new BigDecimal("6.5")));
        barDao.insert(createBar(BarTypes.STRAIGHT, new BigDecimal("9.0")));
        barDao.insert(createBar(BarTypes.DUMBBELL, new BigDecimal("2.3")));
        barDao.insert(createBar(BarTypes.DOUBLE_DUMBBELL, new BigDecimal("2.3")));
    }

    private  static Bar createBar(BarTypes type, BigDecimal weight) {
        Bar bar = new Bar();
        bar.setWeight(weight);
        bar.setBarType(type);
        bar.setUnit(MeasurementUnit.KILOGRAM);
        return bar;
    }

    private void fillDisksInDatabase() {
        final DiskDao diskDao = db.diskDao();
        float[] weights = new float[]{1.25f, 1.25f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f,
                5.0f, 5.0f, 15.0f, 15.0f, 15.0f, 15.0f, 20.0f, 20.0f};
        diskDao.insertAll(TestUtils.genDisks(MeasurementUnit.KILOGRAM, weights));
    }

    private String toString(AssembledBar assembledBar) {
        StringBuilder sb = new StringBuilder();
        sb.append("total: ").append(assembledBar.getWeightInKg()).append("; ")
                .append(assembledBar.getBar().getBarType().name()).append(" ").append(assembledBar.getBar().getWeight())
                .append("; left:").append(diskConverter.fromDisks(assembledBar.getLeftDisks()))
                .append("; right:").append(diskConverter.fromDisks(assembledBar.getRightDisks()));
        return sb.toString();
    }

    private String[] assembledBarsToStingArray(List<AssembledBar> assembledBars) {
        List<String> list = new ArrayList<>();
        for (AssembledBar asb : assembledBars) {
            list.add(toString(asb));
        }
        return list.toArray(new String[0]);
    }
}
