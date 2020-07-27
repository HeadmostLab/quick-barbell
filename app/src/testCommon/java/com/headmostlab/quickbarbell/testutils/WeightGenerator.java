package com.headmostlab.quickbarbell.testutils;

import java.math.BigDecimal;
import java.util.Random;

public class WeightGenerator {

    private static final Random random = new Random();

    public static BigDecimal next() {
        return BigDecimal.valueOf(random.nextFloat()*150);
    }
}
