package com.headmostlab.quickbarbell.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtils {

    public static String toString(BigDecimal decimal) {
        final String s = round(decimal).toString();
        return (s.indexOf('.') != -1) ? s.replaceFirst("\\.?0*$","") : s;
    }

    public static BigDecimal round(BigDecimal bd) {
        return bd.setScale(2, RoundingMode.HALF_UP);
    }
}
