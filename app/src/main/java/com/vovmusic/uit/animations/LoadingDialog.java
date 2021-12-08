package com.vovmusic.uit.animations;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.vovmusic.uit.R;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog alertDialog;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void Start_Loading() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this.activity, R.style.WrapContentDialog);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_loading_dialog, null);
        alertBuilder.setView(view);

        TextView tvLoading = view.findViewById(R.id.tvLoading);
        tvLoading.setSelected(true);
        alertBuilder.setCancelable(false);
        this.alertDialog = alertBuilder.create();

        this.alertDialog.show();
    }

    public void Cancel_Loading() {
        this.alertDialog.dismiss();
    }
}
