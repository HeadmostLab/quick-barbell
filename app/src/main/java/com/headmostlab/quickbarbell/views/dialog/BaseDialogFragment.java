package com.headmostlab.quickbarbell.views.dialog;

import android.os.Bundle;

import com.headmostlab.quickbarbell.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class BaseDialogFragment extends DialogFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
    }

}
