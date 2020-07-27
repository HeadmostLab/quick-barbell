package com.headmostlab.quickbarbell.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.headmostlab.quickbarbell.R;
import com.headmostlab.quickbarbell.business.BarTypes;
import com.headmostlab.quickbarbell.model.MeasurementUnit;

import java.math.BigDecimal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BarDialog extends BaseDialogFragment {

    private static final BigDecimal MIN_WEIGHT = new BigDecimal("0.0");
    private static final BigDecimal MAX_WEIGHT = new BigDecimal("99.9");

    private BarDialogListener listener;

    private BarTypes barType;
    private MeasurementUnit unit;

    @BindView(R.id.weight)
    EditText barWeightEditText;

    @BindView(R.id.weightLayout)
    TextInputLayout barWeightLayout;

    @BindView(R.id.barType)
    AutoCompleteTextView barTypeTextView;

    @BindView(R.id.switchView)
    MaterialButtonToggleGroup switchView;

    @BindView(R.id.kgButton)
    MaterialButton kgButton;

    @BindView(R.id.lbButton)
    MaterialButton lbButton;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (BarDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() +
                    " must implement BarDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogRootView = inflater.inflate(R.layout.dialog_edit_bar, null);

        ButterKnife.bind(this, dialogRootView);

        setUpWidgets();

        AlertDialog alertDialog = new MaterialAlertDialogBuilder(getActivity())
                .setView(dialogRootView).setPositiveButton("Ok", null).setNegativeButton(R.string.dialog_button_cancel, null).create();

        alertDialog.setOnShowListener(dialogInterface -> {
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view -> {
                if (validateForm()) {
                    BigDecimal weight = new BigDecimal(barWeightEditText.getEditableText().toString());
                    listener.onDialogPositiveClick(unit, weight, barType);
                    dialogInterface.dismiss();
                }
            });

            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(view -> dialogInterface.cancel());

        });

        return alertDialog;
    }

    private void setUpWidgets() {
        barWeightEditText.setText(getArguments().getString("weight"));
        setUpSwitch();
        setUpBarTypeTextView();
    }

    private void setUpSwitch() {
        unit = MeasurementUnit.valueOf(getArguments().getString("unit"));
        showUnit(unit);
        switchView.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                switch (checkedId) {
                    case R.id.kgButton:
                        unit = MeasurementUnit.KILOGRAM;
                        break;
                    case R.id.lbButton:
                        unit = MeasurementUnit.POUND;
                        break;
                }
            }
        });
    }

    private void showUnit(MeasurementUnit unit) {
        switch(unit) {
            case KILOGRAM:
                kgButton.toggle();
                break;
            case POUND:
                lbButton.toggle();
                break;
        }
    }

    private void setUpBarTypeTextView() {
        barType = BarTypes.valueOf(getArguments().getString("barType"));
        final String[] stringArray = getResources().getStringArray(R.array.bar_types);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_popup_item, stringArray);
        barTypeTextView.setAdapter(adapter);
        barTypeTextView.setKeyListener(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            barTypeTextView.setText(adapter.getItem(barType.ordinal()), false);
        }
        barTypeTextView.setOnItemClickListener((parent, view1, position, id) -> barType = BarTypes.values()[position]);
    }

    private boolean validateForm() {

        boolean result = true;

        final String weightString = barWeightEditText.getText().toString();
        if (weightString.length() == 0) {
            barWeightLayout.setError(getString(R.string.dialog_field_mandatory));
            result = false;
        } else {
            BigDecimal weight = new BigDecimal(weightString).stripTrailingZeros();
            if (weight.scale() > 1) {
                barWeightLayout.setError(getString(R.string.dialog_bar_weight_scale));
                result = false;
            } else {
                if (weight.compareTo(MAX_WEIGHT) > 0  || weight.compareTo(MIN_WEIGHT) < 0) {
                    barWeightLayout.setError(getString(R.string.dialog_range_error, MIN_WEIGHT.toString(), MAX_WEIGHT.toString()));
                    result = false;
                } else {
                    barWeightLayout.setErrorEnabled(false);
                }
            }
        }
        return result;
    }

    public interface BarDialogListener {
        void onDialogPositiveClick(MeasurementUnit unit, BigDecimal weight, BarTypes barType);
    }
}
