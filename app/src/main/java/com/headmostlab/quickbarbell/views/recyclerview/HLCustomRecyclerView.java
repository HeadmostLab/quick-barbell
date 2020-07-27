package com.headmostlab.quickbarbell.views.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.utils.MetricsUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class HLCustomRecyclerView extends HLCustomRecyclerBaseView {

    private final static int DEF_LINE_STROKE_WIDTH = 2;
    private final static int DEF_COLOR = 0xff464646;

    private Path mLinePath;
    private Paint mLinePaint;
    private float mBorderStrokeWidth = MetricsUtils.dp2px(DEF_LINE_STROKE_WIDTH);
    private float scrollY;
    private float dashWidth = MetricsUtils.dp2px(10+5);
    private int mLineColor = DEF_COLOR;

    public HLCustomRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public HLCustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.customRecyclerViewStyle);
    }

    public HLCustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HLCustomRecyclerView, defStyleAttr, 0);

        try {
            mLineColor = a.getColor(R.styleable.HLCustomRecyclerView_lineColor, mLineColor);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        mLinePath = new Path();
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(mBorderStrokeWidth);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setPathEffect(new DashPathEffect(new float[]{MetricsUtils.dp2px(10),MetricsUtils.dp2px(5)}, 0));

        addOnScrollListener(new MyOnScrollListener());
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mLinePath.reset();
        float dy = scrollY%dashWidth;
        mLinePath.moveTo(getWidth()*0.6f, -dashWidth-dy);
        mLinePath.lineTo(getWidth()*0.6f, getHeight());
        canvas.drawPath(mLinePath, mLinePaint);
    }

    private class MyOnScrollListener extends OnScrollListener {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollY += dy;
        }
    }
}
