package com.adam.rvc.listener;

import android.content.Context;
import com.adam.rvc.service.RVCServiceFactory;

abstract class VolumeListener {

    private static final String VOLUME_MESSAGE_HEADER = "vol";

    private final Context context;

    protected VolumeListener(Context context) {
        this.context = context;
    }

    public void updateVolume(int progress) {
        context.startService(RVCServiceFactory.writeToServer(context, createMessage(progress)));
    }

    private String createMessage(int progress) {
        return VOLUME_MESSAGE_HEADER + progress;
    }

}
