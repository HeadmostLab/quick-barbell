package com.headmostlab.quickbarbell.views.opengl.carouseluniversal.scenes.assembledbarscenes;

import android.content.Context;

import com.headmostlab.openglengine.entities.Entity;
import com.headmostlab.openglengine.models.IRawModel;
import com.headmostlab.openglengine.models.TexturedModel;
import com.headmostlab.openglengine.renderengine.OBJLoader;
import com.headmostlab.openglengine.textures.ModelTexture;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.AssembledBarCard;

public class CurlyBarCardScene extends AssembledBarCardScene {

    private Entity barEntity;

    public CurlyBarCardScene(Context context) {
        super(context);
    }

    @Override
    protected void createScene() {
        super.createScene();

        IRawModel barModel = OBJLoader.loadObjModel(context, R.raw.ez_bar2_smooth2, loader);
        ModelTexture barTexture = new ModelTexture(loader.load2DTexture(R.raw.white_texture));
        TexturedModel barTexturedModel = new TexturedModel(barModel, barTexture);
        barEntity = new Entity(barTexturedModel, -0.057923f, .031072f, 0.26f, 26.6989f, -45.7246f, -12.8155f, 0.096f, 0.096f, 0.096f); // EZ BAR
        importantEntities.add(barEntity);
    }

    @Override
    public void setCard(AssembledBarCard card) {
        super.setCard(card);

        if (card != null) {
            setDisks(barEntity, card.getAssembledBar().getLeftDisks(), true);
            setDisks(barEntity, card.getAssembledBar().getRightDisks(), false);
        }
    }

    @Override
    protected void setPositions() {
        light2.setX(0.349f);
        light2.setY(0.05f);
        light2.setZ(6.0f);
        light.setX(-0.274f);
        light.setY(0.147f);
        light.setZ(1.582f);
        barEntity.setRotX(27.0f);
        barEntity.setRotY(322.0f);
        barEntity.setRotZ(0.0f);
        barEntity.setX(-0.022f);
        barEntity.setY(0.007f);
        barEntity.setZ(0.538f);
    }

}
