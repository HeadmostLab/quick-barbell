package com.headmostlab.quickbarbell.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.utils.MetricsUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatTextView;

public class HLSwitch extends LinearLayout {

    private static float DEF_FONT_SIZE = MetricsUtils.sp2px(20);

    private Paint paint;
    private RectF rect;
    private float strokeWidth;
    private List<TextView> buttons = new ArrayList<>();
    private int current = 0;
    private AttributeSet attrs;
    private int currentColor = 0xffd1d1d1;
    private int otherColor = 0xff565656;
    private OnClick listener;
    private OnClickListener clickListener;
    private float fontSize;

    public HLSwitch(Context context) {
        this(context, null);
    }

    public HLSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HLSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attrs = attrs;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HLSwitch, defStyleAttr, 0);
        try {
            fontSize = a.getDimension(R.styleable.HLSwitch_fontSize, DEF_FONT_SIZE);
        } finally {
            a.recycle();
        }

        init();
    }


    public void setListener(OnClick listener) {
        this.listener = listener;
    }

    public void setButtons(String[] buttons) {
        this.buttons.clear();
        for (String button : buttons) {
            this.buttons.add(createButton(button));
        }
        setCurrent(0);
        addButtons(this.buttons);
        redraw();
    }

    private void addButtons(List<TextView> buttons) {
        removeAllViews();
        for (TextView button : buttons) {
            addView(button);
        }
    }

    public void setCurrent(int cur) {
        current = cur;
        updateColor();
    }

    private void redraw() {
        requestLayout();
        postInvalidate();
    }

    private void updateColor() {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setTextColor(i == current ? currentColor : otherColor);
        }
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        strokeWidth = MetricsUtils.dp2px(3);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(0xff535353);

        rect = new RectF();

        this.setOrientation(LinearLayout.HORIZONTAL);

        final int paddingLeftRight = MetricsUtils.dp2px(10);
        final int paddingTopBottom = MetricsUtils.dp2px(5);
        this.setPadding(paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingTopBottom);

        clickListener = v -> {
            setCurrent(buttons.indexOf(v));
            updateColor();
            if (listener != null) {
                listener.onClick(current);
            }
        };
    }

    private TextView createButton(String label) {
        final int padding = MetricsUtils.dp2px(5);
        final TextView button = new AppCompatTextView(getContext(), attrs);
        button.setText(label);
        button.setPadding(padding, 0, padding, 0);
        button.setGravity(Gravity.CENTER);
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        button.setOnClickListener(clickListener);
        return button;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        float width = (float) getWidth();
        float height = (float) getHeight();

        float hs = strokeWidth * 0.5f;
        float r = height / 2f;
        rect.set(0 + hs, 0 + hs, width - hs, height - hs);
        canvas.drawRoundRect(rect, r, r, paint);
    }

    public interface OnClick {
        void onClick(int current);
    }
}
