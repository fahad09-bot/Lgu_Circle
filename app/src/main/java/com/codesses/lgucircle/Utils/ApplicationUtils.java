/********************************
 * Developed By: Codesses
 * Developer Name: Saad Iftikhar
 *
 */

package com.codesses.lgucircle.Utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.regex.Pattern;


public class ApplicationUtils {

    public static final String DEFAULT_IMAGE="https://firebasestorage.googleapis.com/v0/b/lgu-circle.appspot.com/o/default_image.jpg?alt=media&token=4c18e4f8-6d9e-4ea4-84f3-b0101fdfec09";
    public static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";

    //    TODO: Constants
    public static final int IMAGE_PERMISSION_CODE = 1000;
    public static final int PICK_IMAGE_CODE = 1001;



    /*******************************
     * Method for network is in working state or not.
     ******************************/
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        return false;
    }

    /*******************************
     * Hide keyboard from edit text
     ******************************/
    public static void hideKeyboard(Activity context) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = context.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$");
        return pattern.matcher(password).find();
    }

    public static boolean isUrlValid(String url)
    {
        Pattern pattern = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");
        return pattern.matcher(url).find();
    }

}
