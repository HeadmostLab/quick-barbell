package com.headmostlab.quickbarbell.views.opengl.carouseluniversal.scenes;

import android.content.Context;

import com.headmostlab.openglengine.font.mesh.FontType;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.Card;

public class TextCardScene<T extends Card> extends CardScene<T> {

    protected FontType font;

    public TextCardScene(Context context) {
        super(context);
    }

    @Override
    protected void createScene() {
        super.createScene();

        int fontAtlas = loader.load2DTexture(R.raw.comic_sans_ms_texture);
        font = new FontType(context, fontAtlas, R.raw.comic_sans_ms);
    }
}
