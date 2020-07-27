package com.headmostlab.quickbarbell.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.headmostlab.quickbarbell.R;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

public class HLDigitalFlywheel extends View {

    private final static boolean DEF_SHOW_BORDER = false;
    private final static int RESOURCE_NOT_DEFINED = -1;
    private final static int DEF_TEXT_SIZE = 50;
    private final static int DEF_POINT_SPACE = 50;
    private final static int DEF_BORDER_STROKE_WIDTH = 2;
    private final static int DEF_COLOR = 0xffcccccc;

    private boolean mShowBorder = DEF_SHOW_BORDER;
    private int mTextTypefaceResId = RESOURCE_NOT_DEFINED;
    private float mTextSize = sp2Px(DEF_TEXT_SIZE);
    private int mTextColor = DEF_COLOR;
    private int mBorderColor = DEF_COLOR;

    private Paint mTextPaint;
    private Paint mGradientPaint;
    private Paint mBorderPaint;
    private float mCx;
    private float mCy;
    private Rect mTextRect;
    private GestureDetector mDetector;
    private Scroller mScroller;
    private ValueAnimator mScrollAnimator;
    private int mPointSpace = dp2Px(DEF_POINT_SPACE);
    private int mBorderStrokeWidth = dp2Px(DEF_BORDER_STROKE_WIDTH);
    private int mHeight;
    private int mWidth;
    private int curNum;
    private int curNumAnimated;
    private Matrix mGradientMatrix;
    private LinearGradient mLinearGradient;
    private Path mBorderPath;
    private int scrollYAtDown;
    private OnCurNumChanged clientOnCurNumChangedListener;
    private OnCurNumAnimatedChanged clientOnCurNumAnimatedChangedListener;
    private MyScrollAnimatorListener scrollAnimatorListener;

    public HLDigitalFlywheel(Context context) {
        this(context, null);
    }

    public HLDigitalFlywheel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.digitalFlywheelStyle);
    }

    public HLDigitalFlywheel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HLDigitalFlywheel, defStyleAttr, 0);

        try {
            mShowBorder = a.getBoolean(R.styleable.HLDigitalFlywheel_showBorder, mShowBorder);
            mTextTypefaceResId = a.getResourceId(R.styleable.HLDigitalFlywheel_fontFamily, mTextTypefaceResId);
            mTextSize = a.getDimension(R.styleable.HLDigitalFlywheel_android_textSize, mTextSize);
            mPointSpace = (int) a.getDimension(R.styleable.HLDigitalFlywheel_digitSpace, mPointSpace);
            mBorderStrokeWidth = (int) a.getDimension(R.styleable.HLDigitalFlywheel_borderWidth, mBorderStrokeWidth);
            mTextColor = a.getColor(R.styleable.HLDigitalFlywheel_android_textColor, mTextColor);
            mBorderColor = a.getColor(R.styleable.HLDigitalFlywheel_borderColor, mBorderColor);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        if (mTextTypefaceResId != RESOURCE_NOT_DEFINED) {
            mTextPaint.setTypeface(ResourcesCompat.getFont(getContext(), mTextTypefaceResId));
        }
        mTextRect = new Rect();
        mGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGradientPaint.setStyle(Paint.Style.FILL);
        mGradientMatrix = new Matrix();
        mDetector = new GestureDetector(getContext(), new MyGestureDetectorListener());
        mScroller = new Scroller(getContext(), null, true);
        mScrollAnimator = ValueAnimator.ofFloat(0,1);
        mScrollAnimator.setDuration(5000);
        scrollAnimatorListener = new MyScrollAnimatorListener();
        mScrollAnimator.addUpdateListener(scrollAnimatorListener);
        mBorderPath = new Path();
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderStrokeWidth);
        mBorderPaint.setStrokeCap(Paint.Cap.ROUND);
        mBorderPaint.setPathEffect(new DashPathEffect(new float[]{dp2Px(10),dp2Px(5)}, 0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCx =  w/2f;//getPaddingLeft()+(w-getPaddingLeft()-getPaddingRight())/2f;
        mCy = h/2f;//getPaddingTop()+(h-getPaddingTop()-getPaddingBottom())/2f;
        mHeight = h;
        mWidth = w;
        mLinearGradient = new LinearGradient(0, 0, 0, mHeight/2f, 0xff000000, 0x00000000, Shader.TileMode.MIRROR);
        mGradientPaint.setShader(mLinearGradient);
        scrollToCurNum(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int sy = getScrollY();
        int h = canvas.getHeight();
        int middle = (int) (sy+h/2f);
        int firstPointY = getNextPointY2(sy)-mPointSpace;
        for (int pointY = firstPointY; pointY < (mPointSpace*6+sy); pointY+=mPointSpace) {
            float textCenterY = pointY+mPointSpace/2f;
            float delta = Math.abs(middle - textCenterY);
            float per = (float) (1-Math.min(Math.pow(delta/(h/2f),1.2f),1));
            mTextPaint.setTextSize(mTextSize*per);
//            mTextPaint.setAlpha((int) (255*per));
            int num = getNextNumByPosition2(pointY);
            String t = num+"";
            mTextPaint.getTextBounds(t, 0, t.length(), mTextRect);
            canvas.drawText(t, mCx-mTextRect.centerX(), pointY+mPointSpace/2f-mTextRect.exactCenterY(), mTextPaint);
//            canvas.drawCircle(20, pointY, 10, mTextPaint);
        }

        if (mShowBorder) {
            mBorderPath.reset();
            float borderX = mWidth - mBorderStrokeWidth / 2f;
            mBorderPath.moveTo(borderX, sy);
            mBorderPath.lineTo(borderX, sy + mHeight);
            canvas.drawPath(mBorderPath, mBorderPaint);
        }

        mGradientMatrix.reset();
        mGradientMatrix.postTranslate(0, sy);
        mLinearGradient.setLocalMatrix(mGradientMatrix);
        canvas.drawRect(0, sy, mWidth, sy+mHeight, mGradientPaint);
    }

    public int getCurNum() {
        return curNum;
    }

    public int getAnimatedCurNum() {
        curNumAnimated = (mScrollAnimator.isRunning()) ? getCurrentNumByScrollY() : getCurNum();
        return curNumAnimated;
    }

    private int getCurrentNumByScrollY() {
        int sy = getScrollY();
        int h = mHeight;
        int middle = (int) (sy+h/2f);
        return getNextNumByPosition2(getPrevPointY2(middle));
    }

    private int getPrevPointY2(int yposition) {
        final int nextPointY2 = getNextPointY2(yposition);
        return nextPointY2-mPointSpace;
    }

    private int getNextPointY2(int yposition) {
        int div = yposition/mPointSpace;
        return yposition < 0 ? (div*mPointSpace) : (div+((yposition-div*mPointSpace)>0?1:0))*mPointSpace;
    }

    private int getNextNumByPosition2(int yposition) {
        final int nextPointY2 = getNextPointY2(yposition);
        int div = nextPointY2/mPointSpace;
        return nextPointY2 < 0 ? (10+div%10)%10 : (div+((nextPointY2-div*mPointSpace)>0?1:0))%10;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mDetector.onTouchEvent(event);
        final int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            scrollYAtDown = getScrollY();
        }
        if (!result) {
             if (action == MotionEvent.ACTION_UP) {
                if (scrollYAtDown == getScrollY())  {
                    int newCurNum = curNum;
                    if (event.getY()>mHeight/2f) {
                        newCurNum -= 1;
                    } else {
                        newCurNum += 1;
                    }
                    setCurNumAnimated(newCurNum < 0 ? (10+newCurNum%10)%10 : newCurNum);
                } else {
                    scrollToNumCenter();
                    setCurNumInternalWithNotification(getCurrentNumByScrollY());
                }
                result = true;
            } else {
                result = super.onTouchEvent(event);
            }
        }

        return result;
    }

    private void scrollToCurNum(boolean notify) {
        final int sy = getScrollY();
        final int nextPoint = getNextPointY2(sy);
        final int nextNum = getNextNumByPosition2(nextPoint);
        int pointY = (int) (nextPoint + Math.abs(curNum+(nextNum>curNum?10:0)-nextNum) * mPointSpace - mHeight/2f+mPointSpace/2f);
        int dy = pointY - sy;
        mScroller.startScroll(0, sy, 0, dy);
        startAnimation(notify);
    }

    private void startAnimation(boolean notify) {
        scrollAnimatorListener.setNotify(notify);
        mScrollAnimator.start();
    }

    private void setCurNumInternalWithNotification(int num) {
        curNum = num;
        if (clientOnCurNumChangedListener != null) {
            clientOnCurNumChangedListener.curNumChanged(this);
        }
    }
    private void setCurNumInternal(int num) {
        curNum = num;
    }

    private void setCurNumAnimated(int num) {
        setCurNumInternalWithNotification(num%10);
        scrollToCurNum(true);
    }

    public void setCurNum(int num) {
        setCurNumInternal(num%10);
        scrollToCurNum(false);
    }

    private int dp2Px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int sp2Px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    private void scrollToNumCenter() {
        float sy = getScrollY();
        int middle = (int) (sy + mHeight/2f);
        int prevPointY2 = getPrevPointY2(middle)+mPointSpace/2;
        int nextPointY2 = getNextPointY2(middle)+mPointSpace/2;
        int dy = middle-prevPointY2 < nextPointY2-middle ? prevPointY2-middle: middle-nextPointY2;
        mScroller.startScroll(0, (int) sy, 0, dy);
        startAnimation(true);
    }

    public void setOnCurNumChangedListener(OnCurNumChanged listener) {
        clientOnCurNumChangedListener = listener;
    }

    public void setOnCurNumAnimatedChangedListener(OnCurNumAnimatedChanged listener) {
        clientOnCurNumAnimatedChangedListener = listener;
    }

    private class MyGestureDetectorListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SCALE = 6;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mScrollAnimator.cancel();
            scrollBy(0, (int)distanceY);
            postInvalidate();
            curNum = getCurrentNumByScrollY();
            if (clientOnCurNumAnimatedChangedListener != null) {
                clientOnCurNumAnimatedChangedListener.curNumAnimatedChanged(HLDigitalFlywheel.this);
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityY) < dp2Px(1250)) {
                return false;
            }
            int finalVelocityY = (int) (-velocityY / SCALE);
            mScroller.fling(0, getScrollY(), 0, finalVelocityY, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            int finalScrollY = mScroller.getFinalY();
            float halfPointSpace = mPointSpace/2f;
            float mid = mHeight/2f;
            float newFy = finalVelocityY > 0 ?
                getNextPointY2((int) (finalScrollY+mid))+halfPointSpace - mid:
                getPrevPointY2((int) (finalScrollY+mid))-halfPointSpace - mid;
            mScroller.setFinalY ((int) newFy);
            setCurNumInternalWithNotification(getNextNumByPosition2(getPrevPointY2((int) (newFy+mid+(finalVelocityY > 0?halfPointSpace:0)))));
            startAnimation(true);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            mScroller.forceFinished(true);
            return true;
        }
    }


    private class MyScrollAnimatorListener implements ValueAnimator.AnimatorUpdateListener {

        private volatile boolean isNotify;

        public void setNotify(boolean notify) {
            this.isNotify = notify;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            postInvalidate();
            if (!mScroller.isFinished()) {
                mScroller.computeScrollOffset();
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            } else {
                mScrollAnimator.cancel();
            }

            if (isNotify && clientOnCurNumAnimatedChangedListener != null) {
                clientOnCurNumAnimatedChangedListener.curNumAnimatedChanged(HLDigitalFlywheel.this);
            }
        }
    }

    public interface OnCurNumChanged {
        void curNumChanged(HLDigitalFlywheel flywheel);
    }

    public interface OnCurNumAnimatedChanged {
        void curNumAnimatedChanged(HLDigitalFlywheel flywheel);
    }
}