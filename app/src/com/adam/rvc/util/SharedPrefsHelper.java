package com.adam.rvc.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsHelper {

    public static final String PREFS_SHOW_SERVER_DETAILS = "server";
    public static final String PREFS_SHOW_MESSAGE_DETAILS = "show_message";

    private static final String PREFS_LOCATION = "prefs";

    private final SharedPreferences sharedPreferences;

    public SharedPrefsHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_LOCATION, Context.MODE_PRIVATE);
    }

    public void storeServerDetailsSetting(boolean b) {
        putBoolean(PREFS_SHOW_SERVER_DETAILS, b).commit();
    }

    public void storeMessageDetailsSetting(boolean b) {
        putBoolean(PREFS_SHOW_MESSAGE_DETAILS, b).commit();
    }

    private SharedPreferences.Editor putBoolean(String location, boolean b) {
        return sharedPreferences.edit().putBoolean(location, b);
    }

    public boolean getShowMessageSetting() {
        return getBoolean(PREFS_SHOW_MESSAGE_DETAILS);
    }

    public boolean getShowServerDetailsSetting() {
        return getBoolean(PREFS_SHOW_SERVER_DETAILS);
    }

    public boolean getBoolean(String location) {
        return sharedPreferences.getBoolean(location, false);
    }

}
