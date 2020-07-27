package com.headmostlab.quickbarbell.utils;

public class FloatUtils {
    public static String toString(float f) {
        String s;
        if (f%1!=0 ) {
            s = String.format("%.2f",f);
            if (s.charAt(s.length()-1) == '0') {
                s = s.substring(0,s.length()-1);
            }
        } else {
            s = String.format("%d",(int)f);
        }

        return s;
    }
}
