package com.headmostlab.quickbarbell.utils;

import android.content.Context;
import android.content.DialogInterface;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.headmostlab.quickbarbell.R;

public class DialogUtils {
    public static void showDeleteDialog(Context context, int messageResId, DialogInterface.OnClickListener okListener) {
        final String message = messageResId == 0 ? context.getString(R.string.dialog_delete_message, "") :
        context.getString(R.string.dialog_delete_message, context.getString(messageResId));
        new MaterialAlertDialogBuilder(context, R.style.Dialog_Overlay)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_ok, okListener)
                .setNegativeButton(R.string.dialog_cancel, null).show();
    }
}
