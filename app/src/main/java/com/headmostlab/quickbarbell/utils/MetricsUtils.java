package com.headmostlab.quickbarbell.utils;

import android.content.res.Resources;

public class MetricsUtils {

    public static int dp2px(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static float px2dp(float px) {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    public static float sp2px(float sp) {
        return sp * Resources.getSystem().getDisplayMetrics().scaledDensity;
    }

    public static float px2sp(float px) {
        return px / Resources.getSystem().getDisplayMetrics().scaledDensity;
    }
}
