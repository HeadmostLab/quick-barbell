package com.headmostlab.quickbarbell.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.utils.MetricsUtils;

import androidx.annotation.ColorInt;

public class HLDottedScrollBarView extends View {

    private @ColorInt int ordinaryColor = 0xff353535;
    private @ColorInt int currentColor = 0xff999999;
    private Paint ordinaryPaint;
    private Paint currentPaint;

    private int count = 0;
    private int currentItem = 1;
    private float pointSpace = MetricsUtils.dp2px(8);
    private float radius = MetricsUtils.dp2px(4);
    private float curRadius = MetricsUtils.dp2px(5);

    private float widthCenter;
    private float heightCenter;
    private float height;
    private float width;

    private OnCurrentItemChanged clientOnCurrentItemChangedListener;

    public HLDottedScrollBarView(Context context) {
        this(context, null);
    }

    public HLDottedScrollBarView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.dottedScrollBarViewStyle);
    }

    public HLDottedScrollBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HLDottedScrollBarView, defStyleAttr, 0);

        try {
            ordinaryColor = a.getColor(R.styleable.HLDottedScrollBarView_ordinaryColor, ordinaryColor);
            currentColor = a.getColor(R.styleable.HLDottedScrollBarView_currentColor, currentColor);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        ordinaryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ordinaryPaint.setColor(ordinaryColor);
        currentPaint = new Paint(ordinaryPaint);
        currentPaint.setColor(currentColor);
    }

    public void setCount(int count) {
        this.count = count;
        currentItem = 1;
        postInvalidate();
    }

    public void setCurrentItem(int curItem) {
        if (currentItem != curItem) {
            currentItem = curItem;
            postInvalidate();
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                setCurrent(event.getY());
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private void setCurrent(float yPos) {
        int prevCurItem = currentItem;
        float dotsHeight = 2*curRadius + 2*radius * (count-1) + pointSpace * (count-1);
        float curY = (height - dotsHeight) / 2f;
        float curPointY = curY + radius;
        int currrentPoint = 1;
        float minDist = Math.abs(yPos - curPointY);
        for (int i = 1; i < count; i++) {
            curPointY += pointSpace + 2*radius;
            float newMinDist = Math.abs(yPos - curPointY);
            if (newMinDist > minDist) {
                break;
            }
            minDist = newMinDist;
            currrentPoint ++;
        }
        currentItem = currrentPoint;
        postInvalidate();

        if (clientOnCurrentItemChangedListener != null) {
            if (prevCurItem != currentItem) {
                clientOnCurrentItemChangedListener.currentItemChanged(currentItem);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        widthCenter = w/2f;
        heightCenter = h/2f;
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawARGB(255, 0, 100, 0);

        float dotsHeight = 2*curRadius + 2*radius * (count-1) + pointSpace * (count-1);
        float curY = (height - dotsHeight) / 2f;

        for (int i = 0; i < count; i++) {
            if (i+1 == currentItem) {
                canvas.drawCircle(widthCenter, curY+radius, curRadius, currentPaint);
            } else {
                canvas.drawCircle(widthCenter, curY+radius, radius, ordinaryPaint);
            }

            curY += 2 * radius;
            curY += pointSpace;
        }
    }


    public void setOnCurrentItemChangedListener (OnCurrentItemChanged listener) {
        clientOnCurrentItemChangedListener = listener;
    }

    public interface OnCurrentItemChanged {
        void currentItemChanged(int itemNum);
    }
}
