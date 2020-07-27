package com.headmostlab.quickbarbell.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.headmostlab.quickbarbell.R;

import java.math.BigDecimal;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TemplateDialog extends BaseDialogFragment {

    private static final int PERCENT_MIN = 50;
    private static final int PERCENT_MAX = 150;
    private static final int PERCENT_MULTIPLE = 5;

    @BindView(R.id.weightTitle)
    EditText templateName;

    @BindView(R.id.weightTitleLayout)
    TextInputLayout templateNameLayout;

    @BindView(R.id.weight)
    EditText templateWeight;

    @BindView(R.id.weightLayout)
    TextInputLayout templateWeightLayout;

    @BindView(R.id.percent)
    EditText templatePercent;

    @BindView(R.id.percentLayout)
    TextInputLayout templatePercentLayout;

    @BindView(R.id.balanceCheckBox)
    MaterialCheckBox templateBalanced;

    private TemplateDialogListener listener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (TemplateDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() +
                    " must implement TemplateDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogRootView = inflater.inflate(R.layout.dialog_enter_weight_title, null);

        ButterKnife.bind(this, dialogRootView);

        setUpWidgets(getArguments());

        AlertDialog alertDialog = new MaterialAlertDialogBuilder(getActivity())
                .setView(dialogRootView).setPositiveButton("Ok", null).setNegativeButton(R.string.dialog_button_cancel, null).create();

        alertDialog.setOnShowListener(dialogInterface -> {

            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view -> {
                if (validateForm()) {
                    listener.onDialogPositiveClick(
                            templateName.getText().toString(), new BigDecimal(templateWeight.getText().toString()),
                            Integer.parseInt(templatePercent.getText().toString()), templateBalanced.isChecked()
                    );
                    dialogInterface.dismiss();
                }
            });

            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(view -> {
                listener.onDialogNegativeClick(TemplateDialog.this);
                dialogInterface.cancel();
            });

        });

        return alertDialog;
    }

    private void setUpWidgets(Bundle arguments) {
        templateName.setText(arguments.getString("weightTitle"));
        templateWeight.setText(arguments.getString("weight"));
        templatePercent.setText(arguments.getString("percent"));
        templateBalanced.setChecked(arguments.getBoolean("balanced"));
        templateBalanced.setVisibility(arguments.getBoolean("hideBalanced") ? View.GONE : View.VISIBLE);
    }

    private boolean validateForm() {
        boolean result = true;

        if (templateName.getText().toString().trim().length() == 0) {
            templateNameLayout.setError(getString(R.string.dialog_field_mandatory));
            result = false;
        } else {
            templateNameLayout.setErrorEnabled(false);
        }

        if (templateWeight.getText().length() == 0) {
            templateWeightLayout.setError(getString(R.string.dialog_field_mandatory));
            result = false;
        } else {
            templateWeightLayout.setErrorEnabled(false);
        }

        String templatePercentText = templatePercent.getText().toString();
        if (templatePercentText.length() == 0) {
            templatePercentLayout.setError(getString(R.string.dialog_field_mandatory));
            result = false;
        } else {
            int templatePercentInt = Integer.parseInt(templatePercentText);
            if (templatePercentInt > PERCENT_MAX || templatePercentInt < PERCENT_MIN) {
                templatePercentLayout.setError(getString(R.string.dialog_range_error, PERCENT_MIN+"", PERCENT_MAX+""));
                result = false;
            } else if (templatePercentInt % PERCENT_MULTIPLE != 0) {
                templatePercentLayout.setError(getString(R.string.dialog_template_percent_must_be_multiple, PERCENT_MULTIPLE));
                result = false;
            } else {
                templatePercentLayout.setErrorEnabled(false);
            }
        }

        return result;
    }

    public interface TemplateDialogListener {
        void onDialogPositiveClick(String comment, BigDecimal weight, int percent, boolean balanced);
        void onDialogNegativeClick(DialogFragment dialog);
    }

}