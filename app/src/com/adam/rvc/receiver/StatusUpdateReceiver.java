package com.adam.rvc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.adam.rvc.util.StatusUpdater;

public class StatusUpdateReceiver extends BroadcastReceiver {

    private final OnStatusUpdated statusUpdatedListener;

    public StatusUpdateReceiver(OnStatusUpdated statusUpatedListener) {
        this.statusUpdatedListener = statusUpatedListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        receivedBroadcast(intent);
    }

    private void receivedBroadcast(Intent intent) {
        if (intent != null) {
            if (intent.getAction().equals(StatusUpdater.ACTION_STATUS_MESSAGE)) {
                statusUpdatedListener.onStatusUpdate(intent.getStringExtra(StatusUpdater.EXTRA_MESSAGE));
            }
        }
    }

    public interface OnStatusUpdated {

        void onStatusUpdate(String message);

    }

}
