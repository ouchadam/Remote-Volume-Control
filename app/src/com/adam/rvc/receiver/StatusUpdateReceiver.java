package com.adam.rvc.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.adam.rvc.util.StatusUpdater;

public class StatusUpdateReceiver extends BaseReceiver {

    private final OnStatusUpdated statusUpdatedListener;

    public StatusUpdateReceiver(OnStatusUpdated statusUpdatedListener) {
        this.statusUpdatedListener = statusUpdatedListener;
    }

    @Override
    public IntentFilter getIntentFilter() {
        return new IntentFilter(StatusUpdater.ACTION_STATUS_MESSAGE);
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
