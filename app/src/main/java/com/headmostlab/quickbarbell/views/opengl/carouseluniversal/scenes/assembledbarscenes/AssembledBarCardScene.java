package com.headmostlab.quickbarbell.views.opengl.carouseluniversal.scenes.assembledbarscenes;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.BitmapDrawable;

import com.headmostlab.openglengine.entities.Entity;
import com.headmostlab.openglengine.entities.Light;
import com.headmostlab.openglengine.font.mesh.Alignment;
import com.headmostlab.openglengine.font.mesh.GUIText;
import com.headmostlab.openglengine.models.IRawModel;
import com.headmostlab.openglengine.models.TexturedModel;
import com.headmostlab.openglengine.renderengine.OBJLoader;
import com.headmostlab.openglengine.textures.ModelTexture;
import com.headmostlab.openglengine.utils.ColorUtils;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.entities.Disk;
import com.headmostlab.quickbarbell.utils.BigDecimalUtils;
import com.headmostlab.quickbarbell.utils.BitmapUtils;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.AssembledBarCard;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.scenes.TextCardScene;

import java.math.BigDecimal;
import java.util.List;

public abstract class AssembledBarCardScene extends TextCardScene<AssembledBarCard> {

    protected Light light;
    protected Light light2;
    protected TexturedModel diskTexturedModel;
    protected AssembledBarCard card;
    private MeasurementUnit measurementUnit;

    private int diskColor = 0xffffff;
    private int textColor = 0xa3a3a3;
    private int colorFilter = 0xffffff;

    private float[] textColorArr;

    public AssembledBarCardScene(Context context) {
        super(context);
    }

    @Override
    public void init() {
        super.init();
        setPositions();
    }

    protected abstract void setPositions();

    protected void createScene() {
        super.createScene();

        TypedArray a = context.getTheme().obtainStyledAttributes(null, R.styleable.AssembledBarCardScene, R.attr.assembledBarCardSceneStyle, 0);

        try {
            diskColor = a.getColor(R.styleable.AssembledBarCardScene_diskColor, diskColor);
            textColor = a.getColor(R.styleable.AssembledBarCardScene_android_textColor, textColor);
            colorFilter = a.getColor(R.styleable.SelectBarCardScene_cardColorFilter, colorFilter);

        } finally {
            a.recycle();
        }

        textColorArr = ColorUtils.getRGBA(textColor);

        float b = 1.0f;
        light = new Light(+0.3f, 0.15f, 1f, b,b,b, new float[]{1f, 0.1f, 0.3f});
        light2 = new Light(-0.3f, 0.15f, 1f, b,b,b, new float[]{1f, 0.1f, 0.3f});


        lights.clear();
        addLight(light);
        addLight(light2);
//        addLight(new Light(0, 0, 3f, b,b,b, new float[]{1f, 1f, 0.3f}));
        addLight(new Light(0, 0, 5f, b,b,b, new float[]{1f, 0.1f, 0.3f}));

        BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.card_2);
        Bitmap filteredCard = BitmapUtils.applyColorFilter(drawable.getBitmap(), new LightingColorFilter(0xffffff, colorFilter));
        setCardTexture(loader.load2DTexture(filteredCard));

        IRawModel diskModel = OBJLoader.loadObjModel(context, R.raw.disk, loader);
        ModelTexture diskTexture = new ModelTexture(/*loader.load2DTexture(R.raw.white_texture)*/0, diskColor);
        diskTexturedModel = new TexturedModel(diskModel, diskTexture);
    }

    public void setMeasurement(MeasurementUnit unit) {
        this.measurementUnit = unit;
    }

    @Override
    public void setCard(AssembledBarCard card) {
        this.card = card;
        if (card != null) {
            entities.clear();
            texts.clear();
            addText(card);
        }
    }

    private void addText(AssembledBarCard card) {
        final GUIText text = new GUIText(BigDecimalUtils.toString(card.getWeight())+ "", 4, font, 0.4f, Alignment.RIGHT, 0, 0.20f, 0, 0, 0, 0, 1f, 1f, 1f) {{
            setColor(textColorArr[0], textColorArr[1], textColorArr[2]);
//            setColor(0.5f, 0.5f, 0.5f);
            setCharWidth(0.55f);
            setCharEdge(0.05f);
            setCharBorderWidth(0.6f);
            setCharBorderEdge(0.05f);
            setOutlineColor(1,1,1);
        }};
        addText(text);

        BigDecimal offset = card.getWeightOffset();
        String offsetString = BigDecimalUtils.toString(offset);
        final GUIText text2 = new GUIText((offset.compareTo(BigDecimal.ZERO) > 0 ? "+" : "") + offsetString, 3, font, 0.4f, Alignment.RIGHT, 0, 0.07f, 0, 0, 0, 0, 1f, 1f, 1f) {{
            setColor(textColorArr[0], textColorArr[1], textColorArr[2]);
//            setColor(0.5f, 0.5f, 0.5f);
            setCharWidth(0.55f);
            setCharEdge(0.05f);
            setCharBorderWidth(0.6f);
            setCharBorderEdge(0.05f);
            setOutlineColor(1,1,1);
        }};
        addText(text2);
    }

    @Override
    public void update() {
        super.update();

        setPositions();
    }

    // weight [0.25 .. 50.00]
    protected float getDiskScaleFactor(float weight) {
//        float w = weight * measurementUnit.getCoeff().floatValue(); //diskSizeCoeff;
        float percent = (weight-0.25f) / 49.75f;
        return 1 + percent * 4;
    }

    protected void setDisks(final Entity barEntity, List<Disk> disks, boolean isLeft) {
        setDisks(barEntity,disks,isLeft,0);
    }

    protected void setDisks(final Entity barEntity, List<Disk> disks, boolean isLeft, float offset2) {

        if (disks == null) {
            return;
        }

        float dx = 0.014f;
        int max = disks.size();
        float seatLength = 0.1529f;
        float length = (max-1)*dx;
        float offset = seatLength-length; //length>seatLength ? 0 : seatLength-length;

        int i = 0;
        for (Disk disk: disks) {
            float s = getDiskScaleFactor(disk.getWeightInKg().floatValue());
            if (isLeft) {
                addEntity(new Entity(diskTexturedModel, +0.49f - i * dx - offset - offset2, 0, 0.0f, 0, 0, 0, 1f, s, s) {{
                    setGroup(barEntity);
                }});
            } else {
                addEntity(new Entity(diskTexturedModel, -0.49f + i * dx + offset + offset2, 0, 0.0f, 0, 0, 0, 1f, s, s) {{
                    setGroup(barEntity);
                }});
            }
            i++;
        }
    }
}
