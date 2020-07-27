package com.headmostlab.quickbarbell.views.opengl.carouseluniversal;

import android.content.Context;

import com.headmostlab.openglengine.entities.Entity;
import com.headmostlab.openglengine.entities.Light;
import com.headmostlab.openglengine.models.IRawModel;
import com.headmostlab.openglengine.models.TexturedModel;
import com.headmostlab.openglengine.renderengine.Loader;
import com.headmostlab.openglengine.renderengine.OBJLoader;
import com.headmostlab.openglengine.scene.BaseScene;
import com.headmostlab.openglengine.textures.ModelTexture;
import com.headmostlab.openglengine.utils.ColorUtils;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.Card;

import java.util.List;

import androidx.annotation.ColorInt;

class CarouselScene<T extends Card> extends BaseScene {

    public static final int MIN_RADIUS = 1;
    public static final int DEF_LIGHT_COLOR = 0xb3b3b3;

    private float angle = 0;
    private float oldAngle = -1;
    private static final float[] skyColor = new float[]{0, 0, 0, 1};
    private List<T> cards;
    private IRawModel planeModel;
    private volatile float radius = 1f;

    // height + space between cards 2*PI*R = 2 * 3.14 * 1 / 10
    // (помещается 10 штук) = 0.628
    public static final float CARD_HEIGHT = 0.628f;


    public CarouselScene(Context context, Loader loader) {
        this(context, loader, DEF_LIGHT_COLOR);
    }

    public CarouselScene(Context context, Loader loader, @ColorInt int lightColor) {
        super(null);
        setContext(context);
        setLoader(loader);
        createScene(lightColor);
    }

    public void setCards(List<T> cards) {
        this.cards = cards;
        updateRadius();
        clearCards();
        createCards();
        arrangeCards(false);
    }

    private void clearCards() {
        entities.clear();
    }

    private void createScene(int lightColor) {

        float r = ColorUtils.getR(lightColor);
        float g = ColorUtils.getG(lightColor);
        float b = ColorUtils.getB(lightColor);

//        float c = 0.7f;
//        addLight(new Light(+0.5f, 0.15f, 1f, c, c, c));
//        addLight(new Light(-0.3f, 0.15f, 1f, c, c, c));

        addLight(new Light(+0.5f, 0.15f, 1f, r, g, b));
        addLight(new Light(-0.3f, 0.15f, 1f, r, g, b));

        planeModel = OBJLoader.loadObjModel(context, R.raw.plane_inverted, loader);
    }

    private void arrangeCards(boolean checkAngle) {
        if (! checkAngle || angle != oldAngle) {
            float maxCardCount = (float) (2 * Math.PI * radius / CARD_HEIGHT);
            float delta = 360f / maxCardCount;
            for (int i = 0; i < getEntities().size(); i++) {
                float finalAngle = (angle + delta * i);
                float y = (float) (radius * Math.sin(Math.toRadians(-angle - delta * i)));
                float z = (float) (radius * (Math.cos(Math.toRadians(-angle - delta * i)) - 1f));
                Entity entity = getEntities().get(i);
                entity.setRotX(finalAngle);
                entity.setY(y);
                entity.setZ(z);
            }
        }
        oldAngle = angle;
    }

    private void updateRadius() {
        // на одну карточку больше, чтобы был пробел вместо карточки
        radius = (float) Math.max(CARD_HEIGHT *(cards.size()+1)/(2*Math.PI), MIN_RADIUS);
    }

    private void createCards() {
        for (T card : cards) {
            final TexturedModel textureModel = getTextureModel(card.getTextureId());
            final Entity cardEntity =
                    new Entity(textureModel, 0, 0, 0, 0, 0, 0, 1, 0.5657f, 1);
//                    new Entity(textureModel, 0, 0, 0, 0, -20, 0, 1, 1f, 1);
            addEntity(cardEntity);

        }
    }

    private TexturedModel getTextureModel(int textureId) {
        ModelTexture planeTexture = new ModelTexture(textureId) {{
            setHasTransparency(true);
        }};
        return new TexturedModel(planeModel, planeTexture);
    }

    public void onViewSizeChanged(int width, int height) {
        float ratio = (float) width / height;
        camera.setAspectRatio(ratio);
        // 0.3f небольшое отдаление камеры от карусели для размещения боковой навигации (точки)
        camera.setZ((float) (0.5f * (1 / Math.tan(Math.toRadians(camera.getFOV() / 2f)) + 0.15f)
                / ((ratio < 1) ? ratio : 1))); // 0.5f - половина ширины плоскости
        camera.move();
    }

    public void setAngle(float angle) {
        this.angle = angle;
        arrangeCards(true);
    }

    @Override
    public float[] getSkyColour() {
        return skyColor;
    }

}
