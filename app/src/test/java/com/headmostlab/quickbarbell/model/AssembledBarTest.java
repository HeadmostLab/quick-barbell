package com.headmostlab.quickbarbell.model;

import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;
import com.headmostlab.quickbarbell.testutils.TestUtils;

import org.junit.Test;

import java.math.BigDecimal;

import static com.google.common.truth.Truth.assertThat;

public class AssembledBarTest {

    @Test
    public void isCorrectTotalWeight () {
        final AssembledBar assembledBar = new AssembledBar();
        assembledBar.setBar(TestUtils.generateBar(BigDecimal.valueOf(6.5f), MeasurementUnit.KILOGRAM));
        assembledBar.setLeftDisks(TestUtils.genDisks(MeasurementUnit.KILOGRAM, 0.5f, 1.25f, 2.5f, 5f));
        assembledBar.setRightDisks(TestUtils.genDisks(MeasurementUnit.KILOGRAM, 10f, 15f, 20f, 25f));
        assertThat(assembledBar.getWeightInKg()).isEqualTo(BigDecimal.valueOf(85.75f));
    }
}
