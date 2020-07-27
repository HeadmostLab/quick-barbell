package com.headmostlab.quickbarbell.testutils;

import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.entities.WeightTemplate;

import java.util.ArrayList;
import java.util.List;

public class TemplateTestUtils {

    private static final int BAR_ID = 1;
    private static final Sequence intSeq = new Sequence();

    public static List<WeightTemplate> generate(int count) {
        List<WeightTemplate> templates = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            templates.add(generate());
        }
        return templates;
    }

    public static WeightTemplate generate() {
        return new WeightTemplate(intSeq.next(), BAR_ID, WeightGenerator.next(), MeasurementUnit.KILOGRAM, PercentGenerator.next(), StringGenerator.next(10));
    }

}
