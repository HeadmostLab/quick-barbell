package com.headmostlab.quickbarbell.views.opengl.barpreview.scenes;

import android.content.Context;
import android.content.res.TypedArray;

import com.headmostlab.openglengine.entities.Entity;
import com.headmostlab.openglengine.entities.ICamera;
import com.headmostlab.openglengine.entities.Light;
import com.headmostlab.openglengine.entities.LookAtCamera;
import com.headmostlab.openglengine.hl.HLRect;
import com.headmostlab.openglengine.models.IRawModel;
import com.headmostlab.openglengine.models.TexturedModel;
import com.headmostlab.openglengine.renderengine.DisplayManager;
import com.headmostlab.openglengine.renderengine.Loader;
import com.headmostlab.openglengine.renderengine.OBJLoader;
import com.headmostlab.openglengine.scene.BaseScene;
import com.headmostlab.openglengine.textures.ModelTexture;
import com.headmostlab.openglengine.utils.ColorUtils;
import com.headmostlab.openglengine.wall.Mirror;
import com.headmostlab.openglengine.wall.PlaneFactory;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.model.database.entities.Disk;

import java.util.List;

public abstract class AssembledBarScene extends BaseScene {

    public static final int DEF_LIGHT_COLOR = 0xffffff;

    protected float radius;
    protected LookAtCamera lookCamera;
    protected volatile float angle = 0;
    protected TexturedModel diskTexturedModel;
    protected Entity plane;
    protected Mirror mirror;
    protected volatile boolean isAnimated = true;
    protected volatile int direction = 1;

    private int diskColor = 0xffffff;


    public AssembledBarScene(HLRect viewPort, Context context, Loader loader) {
        super(viewPort);
        setContext(context);
        setLoader(loader);
    }

    protected void createScene(int lightColor) {

        TypedArray a = context.getTheme().obtainStyledAttributes(null, R.styleable.AssembledBarScene, R.attr.assembledBarSceneStyle, 0);

        try {
            diskColor = a.getColor(R.styleable.AssembledBarScene_diskColor, diskColor);
        } finally {
            a.recycle();
        }

        lookCamera = new LookAtCamera();

        float r = ColorUtils.getR(lightColor);
        float g = ColorUtils.getG(lightColor);
        float b = ColorUtils.getB(lightColor);

        addLight(new Light(-2,3,+3, r,g,b));
        addLight(new Light(+2,3,-3, r,g,b));

//        float c = 0.7f;
//        addLight(new Light(-2,3,+3, c,c,c));
//        addLight(new Light(+2,3,-3, c,c,c));

        IRawModel diskModel = OBJLoader.loadObjModel(context, R.raw.disk, loader);
        ModelTexture diskTexture = new ModelTexture(/*loader.load2DTexture(R.raw.gray_texture)*/0, diskColor);
        diskTexturedModel = new TexturedModel(diskModel, diskTexture);

        IRawModel planeModel = OBJLoader.loadObjModel(context, R.raw.plane, loader);
        ModelTexture planeTexture = new ModelTexture(0);
        planeTexture.setReflectionFactor(0.15f);
        TexturedModel texturedModel = new TexturedModel(planeModel, planeTexture);
        plane = new Entity(texturedModel, 0,-0.15f,0, -90,0,0, 1.2f, 1.2f, 1.2f);
        mirror = new Mirror(512, PlaneFactory.newEntityPlane(plane));
        planeTexture.setReflectionTexture(mirror.getTexture());
        importantEntities.add(plane);
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    @Override
    public void onViewPortChanged(int width, int height) {
        float ratio = (float) width / height;
        lookCamera.setAspectRatio(ratio);
        radius = (float) (0.5f * (1 / Math.tan(Math.toRadians(camera.getFOV() / 2f)) + 0.15f) / ((ratio < 1) ? ratio : 1));
        lookCamera.setLookAtM(new float[]{0, 0, radius}, new float[]{0,0,0}, new float[]{0,1,0});
    }

    @Override
    public void update() {
        if (isAnimated) {
            angle += 10 * DisplayManager.getFrameTimeSeconds() * direction;
        }

        double angleInRadians2 = Math.toRadians(angle);
        float x = (float) (radius * Math.cos(angleInRadians2));
        float z = (float) (radius * Math.sin(angleInRadians2));
        lookCamera.setLookAtM(new float[]{x, radius*0.5f, z}, new float[]{0,0,0}, new float[]{0,1,0});

        if (mirror != null) {
            mirror.setUpCamera(lookCamera);
        }
    }

    @Override
    public ICamera getCamera() {
        return lookCamera;
    }


    public Mirror getMirror() {
        return mirror;
    }

    public void stopAnimation() {
        isAnimated = false;
    }

    public void resumeAnimation() {
        isAnimated = true;
    }

    public void rotateBar(float dx) {
        angle += dx;
        direction = (int) Math.signum(dx);
    }

    // weight [0.25 .. 50.00]
    protected float getDiskScaleFactor(float weight) {
//        float w = weight * unit.getCoeff().floatValue();
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
        float offset = seatLength-length; // length>seatLength ? 0 : seatLength-length;

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
