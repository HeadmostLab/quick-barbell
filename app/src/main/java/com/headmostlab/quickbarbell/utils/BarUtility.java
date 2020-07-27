package com.headmostlab.quickbarbell.utils;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.business.BarTypes;

public class BarUtility {

    public static int getBarCardIcon(BarTypes barType) {
        switch (barType) {
            case STRAIGHT:
                return R.drawable.card_flat_bar_2;
            case CURLY:
                return R.drawable.card_ez_bar_2;
            case DUMBBELL:
                return R.drawable.card_dumbbell_bar_2;
            case DOUBLE_DUMBBELL:
                return R.drawable.card_double_dumbbell_bar_2;
            default:
                throw new RuntimeException("BarType "+barType.name()+" is not supported");
        }
    }

    public static int getBarIcon(BarTypes barType) {
        switch (barType) {
            case STRAIGHT:
                return R.drawable.flat_bar_2;
            case CURLY:
                return R.drawable.ez_bar_2;
            case DUMBBELL:
                return R.drawable.dumbbell_bar_2;
            case DOUBLE_DUMBBELL:
                return R.drawable.double_dumbbell_bar_2;
            default:
                throw new RuntimeException("BarType "+barType.name()+" is not supported");
        }
    }
}
