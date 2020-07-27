package com.headmostlab.quickbarbell.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class HLGradientView extends View {

    private Paint mGradientPaint;
    private LinearGradient mLinearGradient;

    public HLGradientView(Context context) {
        this(context, null);
    }

    public HLGradientView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HLGradientView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGradientPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mLinearGradient = new LinearGradient(0, 0, 0, h, 0xff000000, 0x00000000, Shader.TileMode.MIRROR);
        mGradientPaint.setShader(mLinearGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mGradientPaint);
    }
}
