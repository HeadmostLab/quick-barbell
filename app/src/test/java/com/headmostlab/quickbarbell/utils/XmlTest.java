package com.headmostlab.quickbarbell.utils;

import com.headmostlab.quickbarbell.testutils.XmlUtils;

import org.junit.Before;
import org.junit.Test;

import javax.inject.Provider;

import static org.assertj.core.api.Assertions.assertThat;

public class XmlTest {

    private XmlParser parser;
    private Provider<Xml> xmlProvider;

    @Before
    public void setUp() throws Exception {
        parser = XmlUtils.getXmlParser();
        xmlProvider = XmlUtils.getXmlProvider();
    }

    @Test
    public void testGetInt() {
        Xml xml = xmlProvider.get();
        xml.putInt("value", 100);
        final Integer value = xml.getInt("value");
        assertThat(value).isEqualTo(100);
    }

    @Test
    public void testGetIntThatNotExists() {
        Xml xml = xmlProvider.get();
        final Integer value = xml.getInt("value");
        assertThat(value).isEqualTo(null);
    }

    @Test
    public void testGetLong() {
        Xml xml = xmlProvider.get();
        xml.putLong("value", 100);
        final Long value = xml.getLong("value");
        assertThat(value).isEqualTo(100);
    }

    @Test
    public void testGetLongThatNotExists() {
        Xml xml = xmlProvider.get();
        final Long value = xml.getLong("value");
        assertThat(value).isEqualTo(null);
    }

    @Test
    public void testPutStrings() {
        Xml xml = xmlProvider.get();
        String[] source = new String[]{"123","456","789"};
        xml.putStrings("values", source);
        final String[] values = xml.getStrings("values");
        assertThat(values).containsExactly(source);
    }

    @Test
    public void testPutLongs() {
        Xml xml = xmlProvider.get();
        long[] source = new long[]{123, 456, 789};
        xml.putLongs("values", source);
        final long[] values = xml.getLongs("values");
        assertThat(values).containsExactly(source);
    }

    @Test
    public void testPutFloats() {
        Xml xml = xmlProvider.get();
        float[] source = new float[]{123.123f, 456.456f, 789.789f};
        xml.putFloats("values", source);
        final float[] values = xml.getFloats("values");
        assertThat(values).containsExactly(source);
    }
}