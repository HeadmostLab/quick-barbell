package com.headmostlab.quickbarbell.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;

import com.headmostlab.quickbarbell.R;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class HLSpinnerProgressBar extends AppCompatTextView {

    private final static float DEF_STROKE_WIDTH = 12;
    private final static int DEF_STROKE_BG_COLOR = 0xffebebeb;
    private final static int DEF_STROKE_PROGRESS_COLOR = 0xffacacac;
    private final static int DEF_PROGRESS = 50;
    private final static int DEF_HANDLE_COLOR = 0xff818181;
    private final static int DEF_HANDLE_RADIUS = 12;
    private final static int DEF_MIN = 0;
    private final static int DEF_MAX = 100;
    private final static boolean DEF_HANDLE_SHOW = false;
    private final static float DEF_MULTIPLE = 1;

    private float mProgress = DEF_PROGRESS;
    private int mStrokeWidth = dp2Px(DEF_STROKE_WIDTH);
    private int mStrokeBgColor = DEF_STROKE_BG_COLOR;
    private int mStrokeProgressColor = DEF_STROKE_PROGRESS_COLOR;
    private volatile float mAnimatedProgress = DEF_PROGRESS;
    private int mHandleColor = DEF_HANDLE_COLOR;
    private int mHandleRadius = dp2Px(DEF_HANDLE_RADIUS);
    private boolean mShowHandle = DEF_HANDLE_SHOW;
    private int min = DEF_MIN;
    private int max = DEF_MAX;
    private float multiple = DEF_MULTIPLE;

    private int mRadius;
    private int mCx;
    private int mCy;
    private int mHandleCx;
    private int mHandleCy;

    private float mSweepAngle;
    private ValueAnimator animator;

    private Paint mStrokeBgPaint;
    private Paint mStrokeProgressPaint;
    private Paint mHandlePaint;

    private RectF rect;

    private OnProgressChanged clientOnProgressChangedListener;

    public HLSpinnerProgressBar(Context context) {
        super(context);
        init(context);
    }

    public HLSpinnerProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.spinnerProgressBarStyle);
    }

    public HLSpinnerProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HLSpinnerProgressBar, defStyleAttr, 0);

        try {
            mStrokeWidth = a.getDimensionPixelSize(R.styleable.HLSpinnerProgressBar_strokeWidth, mStrokeWidth);
            mStrokeBgColor = a.getColor(R.styleable.HLSpinnerProgressBar_strokeBgColor, mStrokeBgColor);
            mStrokeProgressColor = a.getColor(R.styleable.HLSpinnerProgressBar_strokeProgressColor, mStrokeProgressColor);
            mProgress = a.getFloat(R.styleable.HLSpinnerProgressBar_progress, mProgress);
            mAnimatedProgress = mProgress;
            mHandleColor = a.getColor(R.styleable.HLSpinnerProgressBar_handleColor, mHandleColor);
            mHandleRadius = a.getDimensionPixelSize(R.styleable.HLSpinnerProgressBar_handleRadius, mHandleRadius);
            mShowHandle = a.getBoolean(R.styleable.HLSpinnerProgressBar_showHandle, mShowHandle);
            min = a.getInt(R.styleable.HLSpinnerProgressBar_min, min);
            max = a.getInt(R.styleable.HLSpinnerProgressBar_max, max);
            multiple = a.getFloat(R.styleable.HLSpinnerProgressBar_multiple, multiple);
        } finally {
            a.recycle();
        }

        init(context);
    }

    private void init(Context context) {
        setGravity(Gravity.CENTER);
        setText((int) mAnimatedProgress + "%");
        mSweepAngle = 360f * mAnimatedProgress/max;
        updateHandlePosition();
        mStrokeBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokeBgPaint.setColor(mStrokeBgColor);
        mStrokeBgPaint.setStrokeWidth(mStrokeWidth);
        mStrokeBgPaint.setStyle(Paint.Style.STROKE);
        mStrokeBgPaint.setStrokeCap(Paint.Cap.ROUND);
        mStrokeProgressPaint = new Paint(mStrokeBgPaint);
        mStrokeProgressPaint.setColor(mStrokeProgressColor);
        mHandlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHandlePaint.setColor(mHandleColor);
        mHandlePaint.setStyle(Paint.Style.FILL);
        rect = new RectF();
        mHandleRadius = (mShowHandle) ? mHandleRadius : 0;
        animator = new ValueAnimator();
        animator.addUpdateListener(new MyValueAnimatorListener());
    }

    private float calcRotationAngle(float x, float y) {
        float[] v = {mCx-x, mCy-y};
        float len = (float) Math.sqrt(v[0]*v[0]+v[1]*v[1]);
        float [] vn = {v[0]/len, v[1]/len};
        float[] v2n = {-1,0};
        float cosf = (float) ((vn[0]*v2n[0] + vn[1]*v2n[1]) /
                (Math.sqrt(vn[0]*vn[0]+vn[1]*vn[1])* Math.sqrt(v2n[0]*v2n[0]+v2n[1]*v2n[1])));
        float angle = (float) Math.toDegrees(Math.acos(cosf));
        if (y < mCy) {
            angle = 360-angle;
        }
        return angle;
    }

    public void setClientOnProgressChanged(OnProgressChanged listener) {
        clientOnProgressChangedListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mShowHandle) {
            return super.onTouchEvent(event);
        }

        int action = event.getActionMasked();
        switch(action) {
            case (MotionEvent.ACTION_DOWN):
            case (MotionEvent.ACTION_MOVE):
                final float angle2 = calcRotationAngle(event.getX(), event.getY());
                setProgressByHandle(round(angle2/3.6f * ((max-min)*0.01f)+min));
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private float round(float base) {
        return Math.round(base / multiple) * multiple;
    }

    private void setProgressByHandle(float progress) {
        mProgress = Math.max(Math.min(progress, max), min);
        setProgressInternal(mProgress);
        if (clientOnProgressChangedListener != null) {
            clientOnProgressChangedListener.progressChanged(mProgress);
        }
    }

    private void setProgressInternal(float progress) {
        mAnimatedProgress = progress;
        setText(Math.round(mAnimatedProgress) + "%");
        mSweepAngle = 360f * ((mAnimatedProgress-min)/(max-min));
        updateHandlePosition();
        postInvalidate();
    }

    private void updateHandlePosition() {
        if (mShowHandle) {
            double angleInRad = Math.toRadians(mSweepAngle);
            mHandleCx = (int) (mCx + mRadius * Math.cos(angleInRad));
            mHandleCy = (int) (mCy + mRadius * Math.sin(angleInRad));
        }
    }

    public void setProgress(float progress) {
        mProgress = round(Math.max(Math.min(progress, max), min));
        animator.cancel();
        animator.setFloatValues(mAnimatedProgress, (long)mProgress);
        animator.setDuration((long) (Math.abs((mAnimatedProgress-progress)/(max-min)*100f)*5));
        animator.start();
    }

    public float getProgress() {
        return mProgress;
    }

    private int dp2Px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = (Math.min(w-getPaddingLeft()-getPaddingRight(), h-getPaddingTop()-getPaddingBottom())
                - Math.max(mStrokeWidth, mHandleRadius*2))/2;
        mCx = w/2;
        mCy = h/2;
        rect.set(mCx-mRadius, mCy-mRadius, mCx+mRadius, mCy+mRadius);
        updateHandlePosition();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mCx, mCy, mRadius, mStrokeBgPaint);
        canvas.drawArc(rect, 0, mSweepAngle, false, mStrokeProgressPaint);
        if (mShowHandle) {
            canvas.drawCircle(mHandleCx, mHandleCy, mHandleRadius, mHandlePaint);
        }
    }

    private class MyValueAnimatorListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            HLSpinnerProgressBar.this.setProgressInternal((Float) valueAnimator.getAnimatedValue());
        }
    }

    public interface OnProgressChanged {
        void progressChanged(float progress);
    }
}
