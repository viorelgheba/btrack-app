package com.btrack.btrack_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Dialog {

    private String message;

    public Dialog(String msg) {
        message = msg;
    }

    public void popup(Context context, final boolean quit) {
        new AlertDialog.Builder(context)
                .setTitle("Not compatible")
                .setMessage(message)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (quit) {
                            System.exit(0);
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
