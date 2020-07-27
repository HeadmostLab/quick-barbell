package com.headmostlab.quickbarbell.model.database;

import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.converters.DisksConverter;
import com.headmostlab.quickbarbell.model.database.entities.Disk;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.headmostlab.quickbarbell.testutils.TestUtils.genDisks;
import static com.headmostlab.quickbarbell.testutils.TestUtils.getWeights;
import static org.assertj.core.api.Assertions.assertThat;

public class DisksConverterTest {

    private DisksConverter converter;
    private List<Disk> disks;
    private static final String WEIGHTS_STRING = "0.5 1.25 1.25 2.5 5.0";
    private static final float[] WEIGHTS_ARRAY = new float[]{0.5f, 1.25f, 1.25f, 2.5f, 5.0f};

    @Before
    public void init() {
        converter = new DisksConverter();
        disks = genDisks(MeasurementUnit.KILOGRAM, WEIGHTS_ARRAY);
    }

    @Test
    public void fromDisks() {
        assertThat(converter.fromDisks(disks)).matches(WEIGHTS_STRING);
    }

    @Test
    public void toDisks() {
        assertThat(getWeights(converter.toDisks(WEIGHTS_STRING))).isEqualTo(WEIGHTS_ARRAY);
    }
}
