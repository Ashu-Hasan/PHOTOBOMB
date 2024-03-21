package com.ash.photobomb.other.CustomDialog;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.tashila.pleasewait.PleaseWaitDialog;

public class AshDialog {

    Context context;
    PleaseWaitDialog dialog;
    public AshDialog(Context context){
        this.context = context;
        dialog = new PleaseWaitDialog(context);
        dialog.setTitle("Please wait");
        dialog.setMessage("Loading...");
        dialog.setProgressStyle(PleaseWaitDialog.ProgressStyle.LINEAR);
        dialog.setCancelable(false);
        // Close the dialog after 5 seconds

    }

    public AshDialog(Context context, String title, String message){
        this.context = context;
        dialog = new PleaseWaitDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setProgressStyle(PleaseWaitDialog.ProgressStyle.LINEAR);
        dialog.setCancelable(false);

    }

    public void show(){
        dialog.show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isVisible()) {
                    dialog.dismiss();
                }
            }
        }, 5000);
    }
    public void dismiss(){
        dialog.dismiss();
    }

    public boolean isVisible(){
        return dialog.isVisible();
    }

}
