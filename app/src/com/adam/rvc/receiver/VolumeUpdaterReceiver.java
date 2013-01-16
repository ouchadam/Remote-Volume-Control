package com.adam.rvc.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.adam.rvc.util.VolumeUpdater;

public class VolumeUpdaterReceiver extends BaseReceiver {

    private final OnVolumeUpdated volumeUpdatedListener;

    public VolumeUpdaterReceiver(OnVolumeUpdated volumeUpdatedListener) {
        this.volumeUpdatedListener = volumeUpdatedListener;
    }

    @Override
    public IntentFilter getIntentFilter() {
        return new IntentFilter(VolumeUpdater.ACTION_VOLUME_MESSAGE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        receivedBroadcast(intent);
    }

    private void receivedBroadcast(Intent intent) {
        if (intent != null) {
            if (intent.getAction().equals(VolumeUpdater.ACTION_VOLUME_MESSAGE)) {
                volumeUpdatedListener.onVolumeUpdate(intent.getIntExtra(VolumeUpdater.EXTRA_MESSAGE, 0));
            }
        }
    }

    public interface OnVolumeUpdated {

        void onVolumeUpdate(int volume);

    }
}
