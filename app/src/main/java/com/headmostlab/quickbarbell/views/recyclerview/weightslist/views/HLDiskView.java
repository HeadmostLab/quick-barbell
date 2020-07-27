package com.headmostlab.quickbarbell.views.recyclerview.weightslist.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.utils.MetricsUtils;

public class HLDiskView extends View {

    private final static int DEF_STROKE_COLOR = 0xff7c7c7c;
    private final static int DEF_COLOR =  0xffdddddd;
    private final static int DEF_COLOR2 = 0xffbbbbbb;
    private final static int DEF_COLOR3 = 0xff151515;
    private final static float DEF_STROKE_WIDTH = MetricsUtils.dp2px(3);
    private static final float MAX_WEIGHT = 50;

    private float weight = 50f;
    private Paint paint;
    private int color = DEF_COLOR;
    private int strokeColor = DEF_STROKE_COLOR;
    private int color2 = DEF_COLOR2;
    private int color3 = DEF_COLOR3;
    private float radius;
    private float innerRadius = MetricsUtils.dp2px(8);
    private float strokeWidth = DEF_STROKE_WIDTH;

    public HLDiskView(Context context) {
        this(context, null);
    }

    public HLDiskView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.diskViewStyle);
    }

    public HLDiskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HLDiskView, defStyleAttr, 0);

        try {
            strokeColor = a.getColor(R.styleable.HLDiskView_strokeColor, strokeColor);
//            color = a.getColor(R.styleable.HLDiskView_fillColor, color);
//            color2 = a.getColor(R.styleable.HLDiskView_fillColor2, color2);
            color3 = a.getColor(R.styleable.HLDiskView_fillColor3, color3);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = MetricsUtils.dp2px(weight2Dp())/2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(strokeColor);
        canvas.drawCircle(getWidth()/2f, getHeight()/2f, radius-strokeWidth*0.5f, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawCircle(getWidth()/2f, getHeight()/2f, radius-strokeWidth, paint);

        paint.setColor(color2);
        canvas.drawCircle(getWidth()/2f, getHeight()/2f, (radius-innerRadius)*0.5f+innerRadius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(strokeColor);
        canvas.drawCircle(getWidth()/2f, getHeight()/2f, innerRadius, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color3);
        canvas.drawCircle(getWidth()/2f, getHeight()/2f, innerRadius-strokeWidth*0.5f, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int w = resolveSizeAndState(getSuggestedMinimumWidth(), widthMeasureSpec, 0);
        final int h = resolveSizeAndState(getSuggestedMinimumHeight(), heightMeasureSpec, 0);
        setMeasuredDimension(w, h);
    }

    private float weight2Dp () {
//        return 40;// * (weight/MAX_WEIGHT); //120(50kg)..40(0.25kg)
        float w = Math.max(weight - 0.25f, 0.25f);
        return 40 + 80*(w/(MAX_WEIGHT-0.25f));
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return MetricsUtils.dp2px(weight2Dp());
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return MetricsUtils.dp2px(weight2Dp()) +getPaddingTop()+getPaddingBottom();
    }

    public void setWeightInKg(float weight) {
        this.weight = weight;
        requestLayout();
        postInvalidate();
    }
}
