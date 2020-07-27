package com.headmostlab.quickbarbell.views.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.billingclient.api.SkuDetails;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.business.billing.BillingRepository;
import com.headmostlab.quickbarbell.utils.MetricsUtils;

import androidx.annotation.AttrRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopDialog2 {

    private static final int DEFAULT_ELEVATION = 2;
    private static final float DIALOG_WIDTH_PERCENT = 0.8f;
    private static final float DIALOG_MAX_WIDTH = MetricsUtils.dp2px(400);

    @BindView(R.id.subscribeButton)
    Button subscribeButton;

    @BindView(R.id.buyButton)
    Button buyButton;

    @BindView(R.id.shopDialog)
    View dialogRoot;

    private final Activity activity;
    private final Context context;
    private final BillingRepository billingRepository;
    private SkuDetails inapp;
    private SkuDetails sub;

    public ShopDialog2(AppCompatActivity activity, BillingRepository billingRepository) {
        ButterKnife.bind(this, activity);
        this.activity = activity;
        this.context = dialogRoot.getContext();
        this.billingRepository = billingRepository;
        retrieveEntitlements(activity);
        dialogRoot.setBackground(getBackground(R.attr.shopDialogStyle));
        setWidth();
    }

    private void setWidth() {
        final ViewGroup.LayoutParams layoutParams = dialogRoot.getLayoutParams();
        float width = Math.min(activity.getResources().getDisplayMetrics().widthPixels * DIALOG_WIDTH_PERCENT, DIALOG_MAX_WIDTH);
        layoutParams.width = (int) (width);
    }

    private void retrieveEntitlements(LifecycleOwner lifecycleOwner) {
        billingRepository.getInappSkuDetails().observe(lifecycleOwner, details ->
                inapp = details.isEmpty() ? null : details.iterator().next().getSkuDetails());
        billingRepository.getSubSkuDetails().observe(lifecycleOwner, details ->
                sub = details.isEmpty() ? null : details.iterator().next().getSkuDetails());
    }

    @OnClick(R.id.buyButton)
    public void suscribe(View view) {
        if (inapp != null) {
            billingRepository.launchBillingFlow(activity, inapp);
        }
    }

    @OnClick(R.id.subscribeButton)
    public void buy(View view) {
        if (sub != null) {
            billingRepository.launchBillingFlow(activity, sub);
        }
    }

    private MaterialShapeDrawable getBackground(@AttrRes int defStyleAttr) {
        final TypedArray a = context.getTheme().obtainStyledAttributes(null, R.styleable.ShopDialog, defStyleAttr, 0);
        float elevation;
        try {
            elevation = a.getDimension(R.styleable.ShopDialog_elevation, DEFAULT_ELEVATION);
        } finally {
            a.recycle();
        }
        MaterialShapeDrawable drawable = MaterialShapeDrawable.createWithElevationOverlay(context, elevation);
        ShapeAppearanceModel shapeAppearanceModel = ShapeAppearanceModel.builder(context, null, defStyleAttr, 0).build();
        drawable.setShapeAppearanceModel(shapeAppearanceModel);
        return drawable;
    }

}
