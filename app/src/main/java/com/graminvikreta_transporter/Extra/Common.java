package com.graminvikreta_transporter.Extra;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Prasad
 */

public class Common {

    public static final String SHARED_PREF = "userData";
    public static String totalCartCount = "0";


    public static void saveUserData(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getSavedUserData(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF, 0);
        return pref.getString(key, "");

    }


}
