package com.headmostlab.quickbarbell.views.opengl.barpreview.scenes;

import android.content.Context;

import com.headmostlab.openglengine.entities.Entity;
import com.headmostlab.openglengine.hl.HLRect;
import com.headmostlab.openglengine.models.IRawModel;
import com.headmostlab.openglengine.models.TexturedModel;
import com.headmostlab.openglengine.renderengine.Loader;
import com.headmostlab.openglengine.renderengine.OBJLoader;
import com.headmostlab.openglengine.textures.ModelTexture;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;

public class StraightBarScene extends AssembledBarScene {

    private AssembledBar assembledBar;

    public StraightBarScene(AssembledBar assembledBar, HLRect viewPort, Context context, Loader loader, int lightColor) {
        super(viewPort, context, loader);
        this.assembledBar = assembledBar;
        createScene(lightColor);
    }

    @Override
    protected void createScene(int lightColor) {
        super.createScene(lightColor);

        IRawModel barModel = OBJLoader.loadObjModel(context, R.raw.bar, loader);
        ModelTexture barTexture = new ModelTexture(loader.load2DTexture(R.raw.white_texture));
        barTexture.setReflactivity(1f);
        barTexture.setShineDamper(40);
        TexturedModel barTexturedModel = new TexturedModel(barModel, barTexture);
        Entity barEntity = new Entity(barTexturedModel, 0, 0, 0, 0, 0, 0, 1, 1, 1);
        importantEntities.add(barEntity);

        setDisks(barEntity, assembledBar.getLeftDisks(), true);
        setDisks(barEntity, assembledBar.getRightDisks(), false);
    }

}
