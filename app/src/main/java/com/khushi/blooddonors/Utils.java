package com.khushi.blooddonors;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

public class Utils {

    private Dialog dialog;
    private Context context;

    public Utils(Context context) {
        this.context = context;
    }

    public void startLoadingAnimation() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void endLoadingAnimation() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
