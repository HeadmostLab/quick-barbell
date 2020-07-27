package com.headmostlab.quickbarbell.views.recyclerview.memoryview.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.headmostlab.quickbarbell.R;

import androidx.annotation.Nullable;

public class HLAddWeightView extends View {

    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int mRadius;
    private int mCx;
    private int mCy;
    private int mStrokeWidth = dp2Px(2);
    private int mPlusPadding = dp2Px(20);
    private int mColor = 0xffacacac;

    public HLAddWeightView(Context context) {
        this(context, null);
    }

    public HLAddWeightView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.addWeightViewStyle);
    }

    public HLAddWeightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HLAddWeightView, defStyleAttr, 0);

        try {
            mStrokeWidth = (int) a.getDimension(R.styleable.HLAddWeightView_strokeWidth, mStrokeWidth);
            mColor = a.getColor(R.styleable.HLAddWeightView_mainColor, mColor);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w - getPaddingLeft() - getPaddingRight();
        mHeight = h - getPaddingTop() - getPaddingBottom();
        mRadius = Math.min(mWidth, mHeight)/2 - mStrokeWidth/2;
        mCx = getPaddingLeft()+mWidth/2;
        mCy = getPaddingTop()+mHeight/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mCx, mCy, mRadius, mPaint);
        canvas.drawLine(mCx-mWidth/2f+mPlusPadding, mCy, mCx+mWidth/2f-mPlusPadding, mCy, mPaint);
        canvas.drawLine(mCx, mCy-mHeight/2f+mPlusPadding, mCx, mCy+mHeight/2f-mPlusPadding, mPaint);
    }

    private int dp2Px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int sp2Px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}
