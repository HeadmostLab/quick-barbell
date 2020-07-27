package com.headmostlab.quickbarbell.views;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HLMaterialButtonToggle extends MaterialButton {

    public HLMaterialButtonToggle(@NonNull Context context) {
        super(context);
    }

    public HLMaterialButtonToggle(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HLMaterialButtonToggle(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void toggle() {
        if (!isChecked()) {
            super.toggle();
        }
    }
}
