package com.adam.rvc.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefsHelper {

    public static final String PREFS_SHOW_SERVER_DETAILS = "server";
    public static final String PREFS_SHOW_MESSAGE_DETAILS = "show_message";

    private final SharedPreferences sharedPreferences;

    public SharedPrefsHelper(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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
