/********************************
 * Developed By: Codesses
 * Developer Name: Saad Iftikhar
 *
 */

package com.codesses.lgucircle.Dialogs;

import android.content.Context;

public class ProgressDialog {


    private static Context context;
    private static android.app.ProgressDialog progressDialog;




    //        TODO: Initialize Progress Dialog
    public static void InitializeProgressDialog(Context ctx) {
        context = ctx;
        progressDialog = new android.app.ProgressDialog(context);
    }


    //    TODO: Show Progress Dialog
    public static void ShowProgressDialog(Context ctx, int title, int msg) {

        ProgressDialog.InitializeProgressDialog(ctx);

        progressDialog.setTitle(context.getString(title));
        progressDialog.setMessage(context.getString(msg));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    //        TODO: Dismiss Progress Dialog
    public static void DismissProgressDialog() {
        progressDialog.dismiss();
    }

}
