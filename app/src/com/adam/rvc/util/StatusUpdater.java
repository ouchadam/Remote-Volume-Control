package com.adam.rvc.util;

import android.content.Context;
import android.content.Intent;

public class StatusUpdater {

    public static final String ACTION_STATUS_MESSAGE = "com.novoda.com.adam.rvc.util.statusupdater.ACTION_STATUS_MESSAGE";
    public static final String EXTRA_MESSAGE = "com.novoda.com.adam.rvc.util.statusupdater.EXTRA_MESSAGE";

    private final Context context;

    public StatusUpdater(Context context) {
        this.context = context;
    }

    public void updateStatusAndLog(String message) {
        updateStatusAndLog(message, null);
    }

    public void updateStatusAndLog(String message, Throwable e) {
        updateStatus(message);
        Log.log(message);
    }

    public void updateStatus(String message) {
        context.sendBroadcast(createMessageIntent(message));
    }

    private Intent createMessageIntent(String message) {
        Intent intent = new Intent();
        intent.setAction(ACTION_STATUS_MESSAGE);
        intent.putExtra(EXTRA_MESSAGE, message);
        return intent;
    }

}
