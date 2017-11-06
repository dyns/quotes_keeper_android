package com.thealexvasquez.quotes.MUI;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.thealexvasquez.quotes.QuotesConstants;

/**
 * Created by LACAJITA on 6/23/15
 */

public class DeleteAlert {

    public static void showDeleteAlert(Context context, String title, String message, final OnDeleteListener onDeleteListener){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onDeleteListener.onDelete();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(QuotesConstants.ICON_DELETE_ALERT)
                .show();
    }

    public interface OnDeleteListener{
        void onDelete();
    }

}
