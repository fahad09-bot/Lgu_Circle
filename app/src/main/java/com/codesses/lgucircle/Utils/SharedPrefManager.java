/*
 * Developed By: Codesses
 * Developer Name: Saad Iftikhar
 *
 */

package com.codesses.lgucircle.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Enums.SharedPrefKey;


public class SharedPrefManager {

    //    TODO: Data Members
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        return new SharedPrefManager(context);
    }

    public void storeSharedData(SharedPrefKey key, String value) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(mCtx.getString(R.string.shared_preference), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key.toString(), value);
        editor.apply();
    }

    public String getSharedData(SharedPrefKey key) {
        SharedPreferences preferences = mCtx.getSharedPreferences(mCtx.getString(R.string.shared_preference), Context.MODE_PRIVATE);
        return preferences.getString(key.toString(), null);

    }

}
