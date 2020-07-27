package com.headmostlab.quickbarbell.views.opengl.carouseluniversal.scenes;

import android.content.Context;

import com.headmostlab.openglengine.renderengine.FboRenderer;
import com.headmostlab.openglengine.renderengine.Loader;
import com.headmostlab.openglengine.renderengine.MasterRenderer;
import com.headmostlab.openglengine.scene.BaseScene;
import com.headmostlab.openglengine.toolbox.Display;
import com.headmostlab.openglengine.utils.Fbo;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.Card;

public class BaseCardScene<T extends Card> extends BaseScene {

    private volatile boolean initiated;
    protected Fbo fbo;
    protected T card;

    public BaseCardScene(Context context) {
        setContext(context);
        loader = new Loader(context);
    }

    public void init() {
        clearObjects();

        final float density = Display.getDensity();
        int width = (int) (315 * density);
        int height = (int) (178.25f * density);
        onViewPortChanged(width, height);
        fbo = new Fbo(width, height, new Fbo.Options().useColorTextureAttachment().useDepthRenderbufferAttachment());

        initiated = true;
    }

    public boolean isInitiated() {
        return initiated;
    }

    public int draw(MasterRenderer renderer) {
        FboRenderer.render(fbo, this, getCamera(), renderer, false);
        return fbo.extractColorTexture();
    }

    public void setCard(T card) {
        this.card = card;
    }
}
