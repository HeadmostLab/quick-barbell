package com.headmostlab.quickbarbell.testutils;

import java.util.Random;

public class PercentGenerator {

    private static final Random random = new Random();

    public static float next() {
        return random.nextFloat()*250;
    }
}
