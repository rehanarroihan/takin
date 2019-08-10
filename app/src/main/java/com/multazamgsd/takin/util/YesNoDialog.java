package com.multazamgsd.takin.util;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.multazamgsd.takin.R;

public class YesNoDialog {
    private Dialog dialog;
    private String message;
    private YesNoDialogListener mListener;

    public YesNoDialog(Activity activity, YesNoDialogListener listener) {
        dialog = new Dialog(activity);
        this.mListener = listener;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void show() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_yes_no);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) dialog.findViewById(R.id.textViewMessage)).setText(message);

        ((Button) dialog.findViewById(R.id.buttonNo)).setOnClickListener(v -> dialog.dismiss());

        ((Button) dialog.findViewById(R.id.buttonYes)).setOnClickListener(v -> {
            mListener.onYesSelected();
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public interface YesNoDialogListener {
        void onYesSelected();
    }
}
