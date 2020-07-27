package com.headmostlab.quickbarbell.views.opengl.barpreview;

import android.content.Context;
import android.content.res.TypedArray;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.headmostlab.openglengine.renderengine.DisplayManager;
import com.headmostlab.openglengine.renderengine.FboRenderer;
import com.headmostlab.openglengine.renderengine.Loader;
import com.headmostlab.openglengine.renderengine.MasterRenderer;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.business.assembledbar.AssembledBar;
import com.headmostlab.quickbarbell.utils.MetricsUtils;
import com.headmostlab.quickbarbell.views.opengl.barpreview.scenes.AssembledBarScene;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import androidx.annotation.ColorInt;

public class AssembledBarPreviewView extends GLSurfaceView {

    private ThisRender mRender;
    private GestureDetector mDetector;
    private AssembledBar assembledBar;
    final private @ColorInt int lightColor;

    public AssembledBarPreviewView(Context context) {
        this(context, null);
    }
    public AssembledBarPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AssembledBarPreviewView, R.attr.carouselViewStyle, 0);

        try {
            lightColor = a.getColor(R.styleable.AssembledBarPreviewView_lightColor, AssembledBarScene.DEF_LIGHT_COLOR);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        mDetector = new GestureDetector(getContext(), new MyGestureListener());
        mDetector.setIsLongpressEnabled(false);
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);
        mRender = new ThisRender();
        setRenderer(mRender);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean result = mDetector.onTouchEvent(event);

        if (! result) {
            int action = event.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    mRender.scene.resumeAnimation();
                    result = true;
                    break;
                default:
                    result = super.onTouchEvent(event);
            }
        }

        return result;
    }

    public void setAssembledBar(AssembledBar assembledBar) {
        this.assembledBar = assembledBar;
    }

    private class ThisRender implements Renderer {

        private AssembledBarScene scene;
        private MasterRenderer renderer;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            DisplayManager.createDisplay();
            Loader loader = new Loader(getContext());
            scene = BarSceneFactory.create(assembledBar, null, getContext(), loader, lightColor);
            scene.create();
            renderer = new MasterRenderer(getContext(), loader);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES10.glViewport(0,0,width, height);
            scene.onViewPortChanged(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            DisplayManager.updateDisplay();
            scene.update();
            if (scene.getMirror() != null) {
                FboRenderer.render(scene.getMirror(), scene, renderer, false);
            }
            renderer.renderScene(scene);
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float dxDp = MetricsUtils.px2dp(-distanceX);
            float radiusDp = MetricsUtils.px2dp(Math.min(getHeight(), getWidth())) * 0.5f;
            mRender.scene.rotateBar((float) (dxDp * 180 / (Math.PI * 1 * radiusDp)));
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            mRender.scene.stopAnimation();
            return true;
        }

    }

}