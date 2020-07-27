package com.headmostlab.quickbarbell.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;

public class BitmapUtils {

    public static Bitmap applyColorFilter (Bitmap original, ColorFilter filter) {
        Bitmap bitmap = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColorFilter(filter);

        canvas.drawBitmap(original, 0, 0, paint);

        return bitmap;
    }

}
