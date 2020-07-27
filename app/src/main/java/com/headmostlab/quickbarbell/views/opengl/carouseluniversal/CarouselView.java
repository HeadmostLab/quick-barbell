package com.headmostlab.quickbarbell.views.opengl.carouseluniversal;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Scroller;

import com.headmostlab.openglengine.renderengine.Loader;
import com.headmostlab.openglengine.renderengine.MasterRenderer;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.cards.Card;
import com.headmostlab.quickbarbell.views.opengl.carouseluniversal.scenes.BaseCardScene;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import androidx.annotation.ColorInt;

import static android.opengl.GLES20.glViewport;

public class CarouselView<T extends Card> extends GLSurfaceView {

    private ThisRender mRender;
    private ValueAnimator mScrollAnimator;
    private ValueAnimator mScrollAnimator2;
    private GestureDetector mDetector;
    private Scroller mScroller;
    private final List<T> cards;
    private float symax;
    private OnCardChangedListener<T> clientOnCardChangedListener;
    private float yPosAtDown;
    private OnClickListener clientOnClickListener;
    private int curItem = 1;

    final private @ColorInt int lightColor;

    public CarouselView(Context context) {
        this(context, null);
    }

    public CarouselView(Context context, AttributeSet attrs) {
        super(context, attrs);
        cards = new CopyOnWriteArrayList<>();

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CarouselView, R.attr.carouselViewStyle, 0);

        try {
            lightColor = a.getColor(R.styleable.CarouselView_lightColor, CarouselScene.DEF_LIGHT_COLOR);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);
        mRender = new ThisRender();
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mDetector = new GestureDetector(getContext(), new MyGestureDetectorListener());
        mDetector.setIsLongpressEnabled(false);
        mScroller = new Scroller(getContext(), null, true);
        mScrollAnimator = ValueAnimator.ofFloat(0, 1);
        mScrollAnimator.setDuration(5000);
        mScrollAnimator.addUpdateListener(new MyScrollAnimatorListener());
        mScrollAnimator2 = ValueAnimator.ofFloat(0, 1);
        mScrollAnimator2.setDuration(5000);
        mScrollAnimator2.addUpdateListener(new MyScrollAnimator2Listener());
    }

    public void setCards(List<T> cards) {
        scrollTo(0, 0);
        this.cards.clear();
        this.cards.addAll(cards);
        this.mRender.redrawCards();
        float maxAngle = angleBetweenCards() * (cards.size() - 1);
        symax = angleToScrollY(maxAngle);
        requestRender();
    }

    public void redrawCards() {
        this.mRender.redrawCards();
        requestRender();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float maxAngle = angleBetweenCards() * (cards.size() - 1);
        symax = angleToScrollY(maxAngle);

        setCurCard(curItem);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (cards.size() == 0) return false;

        final int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            yPosAtDown = event.getY();
            if (!mScroller.isFinished()) {
                yPosAtDown = -1;
            }
        }
        boolean result = mDetector.onTouchEvent(event);
        if (!result) {
            if (action == MotionEvent.ACTION_UP) {
                float distance = px2dp(Math.abs(yPosAtDown - event.getY()));
                if (distance < 10) {
                    if (clientOnClickListener != null) {
                        clientOnClickListener.onClick(this);
                    }
                }
                scrollToCardCenter();
            }
        }
        return result;
    }

    private float getRadius() {
        return (float) Math.max(CarouselScene.CARD_HEIGHT * (cards.size() + 1) / (2 * Math.PI), CarouselScene.MIN_RADIUS);
    }

    private float angleToScrollY(float angle) {
        float radius = getRadius();
        float heightDp = px2dp(getHeight());
        float sydp = (float) ((angle * Math.PI * radius * heightDp / 2) / 180);
        return dp2Px(sydp);
    }

    private float angleBetweenCards() {
        float radius = getRadius();
        float maxCardCount = (float) (2 * Math.PI * radius / CarouselScene.CARD_HEIGHT);
        return 360f / maxCardCount;
    }

    private float getPrevCardAngle(float curAngle) {
        float deltaAngle = angleBetweenCards();
        int cardIndex = (int) (curAngle / deltaAngle);
        return Math.max(deltaAngle * cardIndex, 0);
    }

    private float getNextCardAngle(float curAngle) {
        float deltaAngle = angleBetweenCards();
        int cardIndex = (int) (curAngle / deltaAngle) + 1;
        return Math.min(deltaAngle * cardIndex, deltaAngle * (cards.size() - 1));
    }

    private float scrollY2Angle(float scrollY) {
        final float radius = getRadius();
        float scrollYdp = px2dp(scrollY);
        return (float) (scrollYdp * 180 / (Math.PI * radius * (px2dp(getHeight()) / 2)));
    }

    private float scrollY2Angle() {
        return scrollY2Angle(getScrollY());
    }

    private float getScrollYOfNearestCard(float scrollY) {
        float curAngle = scrollY2Angle(scrollY);
        float prevAngle = getPrevCardAngle(curAngle);
        float nextAngle = getNextCardAngle(curAngle);
        float finalAngle = (curAngle - prevAngle) < (nextAngle - curAngle) ? prevAngle : nextAngle;
        return angleToScrollY(finalAngle);
    }

    private void scrollToCardCenter() {
        int curSy = getScrollY();
        float finalScrollY = getScrollYOfNearestCard(curSy);
        int deltaSy = (int) (finalScrollY - curSy);
        mScroller.startScroll(0, curSy, 0, deltaSy);
        mScrollAnimator.start();
    }

    public T getCurCard() {
        float scrollY = getScrollYOfNearestCard(getScrollY()) + 5;
        float deltaAngle = angleBetweenCards();
        float curAngle = scrollY2Angle(scrollY);
        int cardIndex = (int) (curAngle / deltaAngle);
        return cards.get(cardIndex);
    }

    private float px2dp(float px) {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    private int dp2Px(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public void setOnCardChangedListener(OnCardChangedListener<T> listener) {
        this.clientOnCardChangedListener = listener;
    }

    public void setCurCard(int itemNum) {
        curItem = itemNum;
        mScroller.forceFinished(true);
        float deltaAngle = angleBetweenCards();
        float cardAngle = (itemNum - 1) * deltaAngle;
        float finalScrollY = angleToScrollY(cardAngle);
        int curSy = getScrollY();
        int deltaSy = (int) (finalScrollY - curSy);
        mScroller.startScroll(0, curSy, 0, deltaSy);
        if (!mScrollAnimator2.isRunning()) {
            mScrollAnimator2.start();
        }
        requestRender();
    }

    private class MyGestureDetectorListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SCALE = 3;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mScrollAnimator.cancel();

            final Card prevCard = getCurCard();

            if (getScrollY() + distanceY < 0) {
                scrollTo(0, 0);
            } else if (getScrollY() + distanceY > symax) {
                scrollTo(0, (int) symax);
            } else {
                scrollBy(0, (int) distanceY);
            }

            if (clientOnCardChangedListener != null) {
                final T newCard = getCurCard();
                if (!prevCard.equals(newCard)) {
                    clientOnCardChangedListener.cardChanged(newCard);
                }
            }

            requestRender();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityY) < dp2Px(1250)) {
                return false;
            }
            int finalVelocityY = (int) (-velocityY / SCALE);
            mScroller.fling(0, getScrollY(), 0, finalVelocityY, 0, 0, 0, (int) symax);
            mScroller.setFinalY((int) getScrollYOfNearestCard(mScroller.getFinalY()));
            mScrollAnimator.start();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            mScroller.forceFinished(true);
            return true;
        }
    }

    private class MyScrollAnimatorListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            final T prevCard = getCurCard();
            if (!mScroller.isFinished()) {
                mScroller.computeScrollOffset();
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            } else {
                mScrollAnimator.cancel();
            }
            if (clientOnCardChangedListener != null) {
                final T newCard = getCurCard();
                if (!prevCard.equals(newCard)) {
                    clientOnCardChangedListener.cardChanged(newCard);
                }
            }
            requestRender();
        }
    }

    private class MyScrollAnimator2Listener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {

            if (!mScroller.isFinished()) {
                mScroller.computeScrollOffset();
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            } else {
                mScrollAnimator2.cancel();
            }

            requestRender();
        }
    }

    public void setCardScene(BaseCardScene<T> scene) {
        queueEvent(() -> {
            mRender.setCardScene(scene);
            requestRender();
        });
    }


    public interface OnCardChangedListener<T> {
        void cardChanged(T card);
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        clientOnClickListener = listener;
    }


    protected class ThisRender implements Renderer {

        private BaseCardScene<T> cardScene;
        private MasterRenderer renderer;
        private Loader loader;
        private CarouselScene<T> scene;
        private volatile boolean cardDrawn;
        private boolean contextRecreated;

        protected void setCardScene(BaseCardScene<T> cardScene) {
            this.cardScene = cardScene;
        }

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            contextRecreated = true;
            cardDrawn = false;
            loader = new Loader(getContext());
            scene = new CarouselScene<>(getContext(), loader, lightColor);
            scene.create();
            renderer = new MasterRenderer(getContext(), loader);
        }

        @Override
        public void onSurfaceChanged(GL10 arg0, int width, int height) {
            glViewport(0, 0, width, height);
            scene.onViewSizeChanged(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {

            if (cardScene == null) {
                return;
            }

            if (!cardScene.isInitiated() || contextRecreated) {
                cardScene.create();
                cardScene.init();
                contextRecreated = false;
                redrawCards();
            }

            if (!cardDrawn) {
                for (T card : cards) {
                    cardScene.setCard(card);
                    card.setTextureId(cardScene.draw(renderer));
                }
                scene.setCards(cards);
                cardDrawn = true;
            }

            scene.setAngle(-scrollY2Angle());
            renderer.renderScene(scene);
        }

        protected void redrawCards() {
            cardDrawn = false;
        }
    }
}