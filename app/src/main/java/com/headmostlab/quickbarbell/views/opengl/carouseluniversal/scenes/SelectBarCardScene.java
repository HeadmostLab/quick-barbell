package com.headmostlab.quickbarbell.views.opengl.carouseluniversal.scenes;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.BitmapDrawable;

import com.headmostlab.openglengine.font.mesh.Alignment;
import com.headmostlab.openglengine.font.mesh.GUIText;
import com.headmostlab.openglengine.utils.ColorUtils;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.utils.BitmapUtils;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.SelectBarCard;

public class SelectBarCardScene extends TextCardScene<SelectBarCard> {

    private int textColor = 0xa3a3a3;
    private int colorFilter = 0xffffff;

    public SelectBarCardScene(Context context) {
        super(context);
    }

    @Override
    public void setCard(SelectBarCard card) {
        super.setCard(card);

        TypedArray a = context.getTheme().obtainStyledAttributes(null, R.styleable.SelectBarCardScene, R.attr.selectBarCardSceneStyle, 0);

        try {
            textColor = a.getColor(R.styleable.SelectBarCardScene_android_textColor, textColor);
            colorFilter = a.getColor(R.styleable.SelectBarCardScene_cardColorFilter, colorFilter);
        } finally {
            a.recycle();
        }

        BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(card.getIcon());
        Bitmap filteredBitmap = BitmapUtils.applyColorFilter(drawable.getBitmap(), new LightingColorFilter(0xffffff, colorFilter));
        setCardTexture(loader.load2DTexture(filteredBitmap));

        texts.clear();

        final GUIText text = new GUIText(card.getWeightString(), 4, font, 0.4f, Alignment.RIGHT, 0, 0.21f, 0, 0, 0, 0, 1f,1,1f);
        final float[] rgbaTextColor = ColorUtils.getRGBA(textColor);
        text.setColor(rgbaTextColor[0], rgbaTextColor[1], rgbaTextColor[2]);
        addText(text);
    }

}
