package com.headmostlab.quickbarbell.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;

public class ResourcesUtils {

    public static @ColorInt int getColor (@AttrRes int colorAttr, Resources.Theme theme) {
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(colorAttr, typedValue, true);
        return typedValue.data;
    }

    public static Drawable getDrawable(Context context, String drawableName) {
        final int drawableId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
        return context.getResources().getDrawable(drawableId);
    }
}
