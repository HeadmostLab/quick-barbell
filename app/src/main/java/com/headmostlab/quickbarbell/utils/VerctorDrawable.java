package com.headmostlab.quickbarbell.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

public class VerctorDrawable {
    public static Drawable getDrawable(Context context, int drawableResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getDrawable(drawableResId);
        } else {
            try {
                return VectorDrawableCompat.create(context.getResources(), drawableResId, null);
            } catch (Resources.NotFoundException e){
                return ContextCompat.getDrawable(context, drawableResId);
            }
        }
    }
}
