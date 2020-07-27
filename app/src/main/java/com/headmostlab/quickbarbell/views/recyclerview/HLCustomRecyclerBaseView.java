package com.headmostlab.quickbarbell.views.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.utils.MetricsUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class HLCustomRecyclerBaseView extends RecyclerView {

    private Paint mBgPaint;
    private RectF rect;
    private float rectRadius = MetricsUtils.dp2px(5);
    private int bgColor = 0xff151515;

    public HLCustomRecyclerBaseView(@NonNull Context context) {
        this(context, null);
    }

    public HLCustomRecyclerBaseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.customRecyclerViewStyle);
    }

    public HLCustomRecyclerBaseView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HLCustomRecyclerView, defStyleAttr, 0);

        try {
            bgColor = a.getColor(R.styleable.HLCustomRecyclerView_backgroundColor, bgColor);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(bgColor);
        rect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rect.set(getPaddingLeft(),getPaddingTop(),w-getPaddingLeft()-getPaddingRight(),h-getPaddingTop()-getPaddingBottom());
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawRoundRect(rect, rectRadius, rectRadius, mBgPaint);
    }

}
