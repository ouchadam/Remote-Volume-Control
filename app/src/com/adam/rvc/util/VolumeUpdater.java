package com.adam.rvc.util;

import android.content.Context;
import com.adam.rvc.receiver.ReceiverIntentFactory;

public class VolumeUpdater {

    public static final String ACTION_VOLUME_MESSAGE = "com.novoda.com.adam.rvc.util.volumeupdater.ACTION_VOLUME_MESSAGE";
    public static final String EXTRA_MESSAGE = "com.novoda.com.adam.rvc.util.volumeupdater.EXTRA_MESSAGE";

    private final Context context;

    public VolumeUpdater(Context context) {
        this.context = context;
    }

    public void updateVolume(int volume) {
        context.sendBroadcast(ReceiverIntentFactory.broadcastServerVolumeMessage(volume));
    }

}
