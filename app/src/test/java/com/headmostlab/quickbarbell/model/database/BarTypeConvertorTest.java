package com.headmostlab.quickbarbell.model.database;

import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.model.database.converters.BarTypeConverter;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class BarTypeConvertorTest {

    private BarTypeConverter convertor;

    @Before
    public void init() {
        convertor = new BarTypeConverter();
    }

    @Test
    public void toBarType () {
        assertThat(convertor.toBarType(BarTypes.CURLY.ordinal())).isEquivalentAccordingToCompareTo(BarTypes.CURLY);
    }

    @Test
    public void fromBarType() {
        assertThat(convertor.fromBarType(BarTypes.CURLY)).isEqualTo(BarTypes.CURLY.ordinal());
    }
}
