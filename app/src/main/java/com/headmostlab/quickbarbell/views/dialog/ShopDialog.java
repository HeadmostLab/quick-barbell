package com.headmostlab.quickbarbell.views.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.billingclient.api.SkuDetails;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.business.billing.BillingRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopDialog extends BaseDialogFragment {

    @BindView(R.id.subscribeButton)
    Button subscribeButton;

    @BindView(R.id.buyButton)
    Button buyButton;

    private final BillingRepository billingRepository;
    private SkuDetails inapp;
    private SkuDetails sub;

    @Inject
    public ShopDialog(BillingRepository billingRepository) {
        this.billingRepository = billingRepository;
    }

    private void subscribe() {
        billingRepository.getInappSkuDetails().observe(getActivity(), details ->
                inapp = details.isEmpty() ? null : details.iterator().next().getSkuDetails());
        billingRepository.getSubSkuDetails().observe(getActivity(), details ->
                sub = details.isEmpty() ? null : details.iterator().next().getSkuDetails());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        subscribe();
        final View view = requireActivity().getLayoutInflater()
                .inflate(R.layout.dialog_acquire_entitlement, null);
        ButterKnife.bind(this, view);
        buyButton.setOnClickListener(v -> {
            if (inapp != null) {
                billingRepository.launchBillingFlow(getActivity(), inapp);
            }
        });
        subscribeButton.setOnClickListener(v -> {
            if (sub != null) {
                billingRepository.launchBillingFlow(getActivity(), sub);
            }
        });
        return new MaterialAlertDialogBuilder(getActivity()).setView(view).create();
    }
}
