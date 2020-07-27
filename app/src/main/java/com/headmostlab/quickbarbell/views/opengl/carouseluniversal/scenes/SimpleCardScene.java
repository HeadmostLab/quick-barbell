package com.headmostlab.quickbarbell.views.opengl.carouseluniversal.scenes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.BitmapDrawable;

import com.headmostlab.quickbarbell.utils.BitmapUtils;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.SimpleCard;

import androidx.annotation.ColorInt;

public class SimpleCardScene<T extends SimpleCard> extends CardScene<T> {

    private boolean isFiltered = false;
    private int colorFilter;

    public SimpleCardScene(Context context) {
        super(context);
    }

    @Override
    public void setCard(T card) {
        super.setCard(card);

        if (isFiltered) {
            Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(card.getIcon())).getBitmap();
            bitmap = BitmapUtils.applyColorFilter(bitmap, new LightingColorFilter(0xffffff, colorFilter));
            setCardTexture(loader.load2DTexture(bitmap));
        } else {
            setCardTexture(loader.load2DTexture(card.getIcon()));
        }

    }

    public void setColorFilter(@ColorInt int color) {
        isFiltered = true;
        colorFilter = color;
    }


}
