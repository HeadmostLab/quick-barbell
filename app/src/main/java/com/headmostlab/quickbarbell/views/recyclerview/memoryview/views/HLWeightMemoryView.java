package com.headmostlab.quickbarbell.views.recyclerview.memoryview.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.model.MeasurementUnit;
import com.headmostlab.quickbarbell.model.database.entities.WeightTemplate;
import com.headmostlab.quickbarbell.utils.VerctorDrawable;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class HLWeightMemoryView extends View {

    public enum Status {
        NORMAL,
        UNCHECKED,
        CHECKED,
        HISTORY
    }

    private Paint mPaint;
    private Paint mHistoryPaint;
    private Paint mTextPaint;
    private Paint mCheckboxStrokePaint;
    private Paint mCheckboxFillPaint;
    private Paint mCheckboxCheckedPaint;
    private Paint mCommentTextPaint;
    private Paint mUnitTextPaint;
    private Paint mPercentTextPaint;
    private Paint mPercentArcPaint;
    private int mWidth;
    private int mHeight;
    private int mRadius;
    private int mCheckboxRadius = dp2Px(10);
    private int mCheckboxCheckedRadius = dp2Px(6);
    private int mCx;
    private int mCy;
    private int mCheckBoxCx;
    private int mCheckBoxCy;
    private int mStrokeWidth = dp2Px(4);
    private int mPercentageStrokeWidth = dp2Px(2);
    private int mPercentArcColor = 0xffffffff;
    private int mPlusPadding = dp2Px(20);
    private int mTextTopPadding = dp2Px(26);
    private int mPercentBottomPadding = dp2Px(10);
    private int mCommentTextTopPadding = dp2Px(45);
    private int mCommentTextPadding = dp2Px(8);
    private int mTextSize = sp2Px(18);
    private int mUnitTextSize = sp2Px(10);
    private int mCommentTextSize = sp2Px(12);
    private int mPercentTextSize = sp2Px(10);
    private int mColor = 0xffacacac;
    private int mTextColor = 0xffd2d2d2;
    private int mFillColor = 0xff000000;
    private int mHistoryIconColor = 0xff3a3a3a;
    private int mHistoryBackgroundColor = 0xff3a3a3a;

    private String mWeightText;
    private String mUnitText;
    private String mCommentText = "Присед...";
    private Path commentPath;
    private int mCommentLastIndex;
    private Rect textRect;
    private RectF arcRect;
    private int weight;
    private MeasurementUnit unit;
    private float percent = 100;
    private float percentMin = 50;
    private float percentMax = 150;
    private String mPercentText = percent + "%";
    private Status status = Status.NORMAL;

    private WeightTemplate weightTemplate;
    private Drawable history;

    public HLWeightMemoryView(Context context) {
        this(context, null);
    }

    public HLWeightMemoryView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.weightMemoryViewStyle);
    }

    public HLWeightMemoryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HLWeightMemoryView, defStyleAttr, 0);

        try {
            mTextSize = (int) a.getDimension(R.styleable.HLWeightMemoryView_weightTextSize, mTextSize);
            mCommentTextSize = (int) a.getDimension(R.styleable.HLWeightMemoryView_commentTextSize, mCommentTextSize);
            mStrokeWidth = (int) a.getDimension(R.styleable.HLWeightMemoryView_strokeWidth, mStrokeWidth);
            mPercentageStrokeWidth = (int) a.getDimension(R.styleable.HLWeightMemoryView_percentStrokeWidth, mPercentageStrokeWidth);
            mTextTopPadding = (int) a.getDimension(R.styleable.HLWeightMemoryView_weightTextTopPadding, mTextTopPadding);
            mCommentTextPadding = (int) a.getDimension(R.styleable.HLWeightMemoryView_commentTextPadding, mCommentTextPadding);
            mCheckboxRadius = (int) a.getDimension(R.styleable.HLWeightMemoryView_checkboxRadius, mCheckboxRadius);

            mColor = a.getColor(R.styleable.HLWeightMemoryView_mainColor, mColor);
            mPercentArcColor = a.getColor(R.styleable.HLWeightMemoryView_percentArcColor, mPercentArcColor);
            mTextColor = a.getColor(R.styleable.HLWeightMemoryView_android_textColor, mTextColor);
            mHistoryIconColor = a.getColor(R.styleable.HLWeightMemoryView_historyIconColor, mHistoryIconColor);

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
        mHistoryPaint = new Paint(mPaint);
        mHistoryPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mHistoryPaint.setAlpha((int) (256*0.8f));
        mPercentArcPaint = new Paint(mPaint);
        mPercentArcPaint.setColor(mPercentArcColor);
        mPercentArcPaint.setStrokeWidth(mPercentageStrokeWidth);
        mPercentArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mCheckboxStrokePaint = new Paint(mPaint);
        mCheckboxStrokePaint.setStyle(Paint.Style.STROKE);
        mCheckboxFillPaint = new Paint(mPaint);
        mCheckboxFillPaint.setStyle(Paint.Style.FILL);
        mCheckboxFillPaint.setColor(mFillColor);
        mCheckboxCheckedPaint = new Paint(mCheckboxFillPaint);
        mCheckboxCheckedPaint.setColor(mColor);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.comic));
        mCommentTextPaint = new Paint(mTextPaint);
        mCommentTextPaint.setTextSize(mCommentTextSize);
        mUnitTextPaint = new Paint(mTextPaint);
        mUnitTextPaint.setTextSize(mUnitTextSize);
        mPercentTextPaint = new Paint(mCommentTextPaint);
        mPercentTextPaint.setTextSize(mPercentTextSize);
        weight = (int) Math.round(Math.random()*150);
        mWeightText = weight+"";
        textRect = new Rect();
        arcRect = new RectF();
        commentPath = new Path();
        history = VerctorDrawable.getDrawable(getContext(), R.drawable.history);
        DrawableCompat.setTint(history, mHistoryIconColor);
    }

    public WeightTemplate getWeightTemplate() {
        return weightTemplate;
    }

    public void setWeightTemplate(WeightTemplate template) {
        this.weightTemplate = template;
    }

    public void setWeight(int weight) {
        if (this.weight != weight) {
            this.weight = weight;
            mWeightText = weight + "";
            postInvalidate();
        }
    }

    public void setUnit(MeasurementUnit unit) {
        if (this.unit != unit) {
            this.unit = unit;
            mUnitText = unit.getLabel();
            postInvalidate();
        }
    }

    public void setPercent(float percent) {
        if (this.percent != percent) {
            this.percent = percent;
            postInvalidate();
        }
    }

    public float getPercent() {
        return percent;
    }

    public void setComment(String comment) {
        if (! mCommentText.equals(comment)) {
            mCommentText = comment;
            calcTitleWidths();
            updateCommentPath();
            postInvalidate();
        }
    }
    public String getComment() {
        return mCommentText;
    }

    public int getWeight() {
        return weight;
    }

    private float calcTitleWidths() {
        float[] widths = new float[mCommentText.length()];
        mCommentTextPaint.getTextWidths(mCommentText, widths);
        int textMaxWidth = mWidth-dp2Px(10);
        int texCurWidth = 0;
        for (int i = 0; i < widths.length; i++) {
            if (texCurWidth + widths[i] > textMaxWidth) {
                break;
            }
            mCommentLastIndex = i+1;
            texCurWidth += widths[i];
        }
        return texCurWidth;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w - getPaddingLeft() - getPaddingRight();
        mHeight = h - getPaddingTop() - getPaddingBottom();
        mRadius = Math.min(mWidth, mHeight)/2 - mStrokeWidth/2;
        mCx = getPaddingLeft()+mWidth/2;
        mCy = getPaddingTop()+mHeight/2;

        int checkDelta = (int) (mRadius/Math.sqrt(2)); // сторона равнобедренного прямоугольного треугольника
        mCheckBoxCx = mCx - checkDelta;
        mCheckBoxCy = mCy - checkDelta;

        calcTitleWidths();

        float delta = mStrokeWidth/2f - mPercentageStrokeWidth/2f;

        arcRect.set(mCx-mRadius+delta, mCy-mRadius+delta, mCx+mRadius-delta, mCy+mRadius-delta);

        updateCommentPath();

    }

    private void updateCommentPath() {
        commentPath.reset();
        String comment = mCommentText.replaceAll("(.)", "$1 ");
        mCommentTextPaint.getTextBounds(comment, 0, comment.length(), textRect);
        int a = (int) ((textRect.width()*180) / (Math.PI*(mRadius/*-MetricsUtils.dp2px(8)*/) ) * 0.5f);
        commentPath.addArc(arcRect, 90+a, -a*2);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mCx, mCy, mRadius, mPaint);
        canvas.drawArc(arcRect, 0, 360f * Math.abs((percent-percentMin)/(percentMax-percentMin)), false, mPercentArcPaint);

        mTextPaint.getTextBounds(mWeightText, 0, mWeightText.length(), textRect);
        canvas.drawText(mWeightText, mCx-textRect.exactCenterX(), /*mCy-textRect.exactCenterY() */mCy-mRadius+mTextTopPadding, mTextPaint);

        String comment = mCommentText.replaceAll("(.)", "$1 ");

////        mCommentTextPaint.getTextBounds(comment, 0, comment.length()/*mCommentLastIndex*/, textRect);
//        mCommentTextPaint.getTextBounds(mCommentText, 0, mCommentLastIndex, textRect);
//        canvas.drawText(mCommentText, 0, mCommentLastIndex, mCx-textRect.exactCenterX(), mCy-mRadius+mCommentTextTopPadding, mCommentTextPaint);

//        float len = (float) (Math.PI*mRadius);

        canvas.drawTextOnPath(comment, commentPath, 0, -mCommentTextPadding, mCommentTextPaint);
//        canvas.drawPath(commentPath, mPaint);

//        mUnitTextPaint.getTextBounds(mUnitText, 0, mUnitText.length(), textRect);
//        canvas.drawText(mUnitText, mCx-textRect.exactCenterX(),/*mCy-textRect.exactCenterY()+MetricsUtils.dp2px(2) */ mCy+mRadius-mPercentBottomPadding, mUnitTextPaint);


//        mPercentTextPaint.getTextBounds(mPercentText, 0, mPercentText.length(), textRect);
//        canvas.drawText(mPercentText, mCx-textRect.exactCenterX(), mCy+mRadius-mPercentBottomPadding, mPercentTextPaint);


        if (status == Status.UNCHECKED || status == Status.CHECKED) {
            canvas.drawCircle(mCheckBoxCx, mCheckBoxCy, mCheckboxRadius, mCheckboxFillPaint);
            canvas.drawCircle(mCheckBoxCx, mCheckBoxCy, mCheckboxRadius, mCheckboxStrokePaint);
        }

        if (status == Status.CHECKED) {
            canvas.drawCircle(mCheckBoxCx, mCheckBoxCy, mCheckboxCheckedRadius, mCheckboxCheckedPaint);
        }

        if (status == Status.HISTORY) {
            canvas.drawCircle(mCx, mCy, mRadius, mHistoryPaint);
            canvas.save();
            int w = (int) (getWidth() * 0.5f);
            int h = (int) (getHeight() * 0.5f);
            history.setBounds(0, 0, w, h);
            canvas.translate((getWidth() - w) * 0.5f, (getHeight() - h) * 0.5f);
            history.draw(canvas);
            canvas.restore();
        }
    }

    public void setStatus(Status status) {
        if (this.status != status) {
            this.status = status;
            postInvalidate();
        }
    }

    public Status getStatus() {
        return status;
    }

    private int dp2Px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int sp2Px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}
