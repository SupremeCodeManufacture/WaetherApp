package view.custom;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import data.App;
import logic.listeners.OnDualSelectionListener;

public class CustomDialogs {

    public static void createSimpleDialog(final Context context, String title, String msg, boolean haveCancel, String customOkTxt, String customCancelTxt, final OnDualSelectionListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);

        String okTxt = customOkTxt != null ? customOkTxt : App.getAppCtx().getResources().getString(android.R.string.yes);
        builder.setPositiveButton(okTxt,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) listener.onPositiveButtonClick();
                        dialog.dismiss();
                    }
                });

        if (haveCancel) {
            String negativeText = customCancelTxt != null ? customCancelTxt : context.getResources().getString(android.R.string.cancel);
            builder.setNegativeButton(negativeText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (listener != null) listener.onNegativeButtonClick();
                            dialog.dismiss();
                        }
                    });
        }

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
}