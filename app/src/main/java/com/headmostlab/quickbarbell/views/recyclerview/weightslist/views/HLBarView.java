package com.headmostlab.quickbarbell.views.recyclerview.weightslist.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.utils.BarUtility;
import com.headmostlab.quickbarbell.utils.MetricsUtils;

import androidx.annotation.ColorInt;

public class HLBarView extends View {

    private BarTypes barType;
    private ColorFilter colorFilter;
    private @ColorInt int colorMultiply = 0xffffff;

    public HLBarView(Context context) {
        this(context, null);
    }

    public HLBarView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.barViewStyle);
    }

    public HLBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HLBarView, defStyleAttr, 0);

        try {
            colorMultiply = a.getColor(R.styleable.HLBarView_colorMultiply, colorMultiply);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        this.barType = BarTypes.STRAIGHT;
        this.colorFilter = new LightingColorFilter(0xffffff, colorMultiply);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable drawable = getResources().getDrawable(BarUtility.getBarIcon(barType));
        drawable.setColorFilter(colorFilter);
        drawable.setBounds(0, 0, getWidth(), getHeight());
        drawable.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int w = resolveSizeAndState(getSuggestedMinimumWidth(), widthMeasureSpec, 0);
        final int h = resolveSizeAndState(getSuggestedMinimumHeight(), heightMeasureSpec, 0);
        setMeasuredDimension(w, h);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return MetricsUtils.dp2px(140);
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return MetricsUtils.dp2px(89);
    }

    public void setBarType(BarTypes barType) {
        this.barType = barType;
        postInvalidate();
    }
}
