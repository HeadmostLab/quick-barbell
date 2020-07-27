package com.headmostlab.quickbarbell.model.database.entities;

import com.headmostlab.quickbarbell.model.MeasurementUnit;

import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class DiskTest {

    @Test
    public void getWeightInKg() {
        final Disk disk = new Disk(BigDecimal.valueOf(20), MeasurementUnit.POUND);
        assertThat(9.07184f).isEqualTo(disk.getWeightInKg().floatValue());
    }
}