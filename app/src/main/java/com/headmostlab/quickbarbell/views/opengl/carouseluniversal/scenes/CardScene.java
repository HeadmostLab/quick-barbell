package com.headmostlab.quickbarbell.views.opengl.carouseluniversal.scenes;

import android.content.Context;

import com.headmostlab.openglengine.entities.Entity;
import com.headmostlab.openglengine.entities.Light;
import com.headmostlab.openglengine.models.IRawModel;
import com.headmostlab.openglengine.models.TexturedModel;
import com.headmostlab.openglengine.renderengine.OBJLoader;
import com.headmostlab.openglengine.textures.ModelTexture;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.Card;

public class CardScene<T extends Card> extends BaseCardScene<T> {

    protected float[] skyColour = new float[]{0, 0, 0, 0};
    protected Entity cardEntity;

    public CardScene(Context context) {
        super(context);
    }

    @Override
    public void init() {
        super.init();
        createScene();
    }

    protected void createScene() {
        addLight(new Light(0, 0, 3f, 1f,1f,1f));

        IRawModel planeModel = OBJLoader.loadObjModel(context, R.raw.plane, loader);
        ModelTexture planeTexture = new ModelTexture(loader.load2DTexture(R.drawable.card_2));
        TexturedModel texturedModel = new TexturedModel(planeModel, planeTexture);
        cardEntity = new Entity(texturedModel, 0, 0, 0, 0, 0, 0, 1, 1 / camera.getAspectRatio(), 1);
        importantEntities.add(cardEntity);

        camera.setFov(50);
        camera.setNearPlane(0.001f);
        camera.setZ((float) (0.5f * (1 / Math.tan(Math.toRadians(camera.getFOV() / 2f)))
                / camera.getAspectRatio())); // 0.5f - половина ширины плоскости
        camera.move();
    }

    @Override
    public float[] getSkyColour() {
        return skyColour;
    }

    public void setCardTexture(int id) {
        cardEntity.getModel().getTexture().setTexture(id);
    }

}
