package com.adam.rvc.util;

public class Log {

    private static final String TAG = "Tribble";

    public static void log(String message) {
        log(message, null);
    }

    public static void log(String message, Throwable e) {
        if (e != null) {
            android.util.Log.e(TAG, message, e);

        } else {
            android.util.Log.i(TAG, message);
        }
    }

}
