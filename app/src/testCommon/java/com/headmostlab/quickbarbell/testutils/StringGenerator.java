package com.headmostlab.quickbarbell.testutils;

import java.util.Random;

public class StringGenerator {
    private static final Random random = new Random();
    private static final String alphabet = "abcdefghigklmnopqrstuvwxyz";
    private static final StringBuilder sb = new StringBuilder();

    public static String next(int count) {
        sb.setLength(0);
        for (int i = 0; i < count; i++) {
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return sb.toString();
    }
}
