package com.adam.rvc.util;

import android.content.Context;
import com.adam.rvc.receiver.ReceiverIntentFactory;

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
        context.sendBroadcast(ReceiverIntentFactory.broadcastStatusMessage(message));
    }

}
