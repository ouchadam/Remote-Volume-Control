package com.adam.rvc.util;

import android.content.Context;
import com.adam.rvc.receiver.OnMessageReceived;
import com.adam.rvc.service.RVCServiceFactory;

public class MessageHandler implements OnMessageReceived {

    public static final String ACTION_SERVER_MESSAGE = "com.novoda.com.adam.rvc.util.messagehandler.ACTION_SERVER_MESSAGE";
    public static final String EXTRA_MESSAGE = "com.novoda.com.adam.rvc.util.messagehandler.EXTRA_MESSAGE";

    private static final String SERVER_DISCONNECT = "1";
    private static final int HEX_RADIX = 16;

    private final Context context;

    public MessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onMessageReceived(String message) {
        Log.log("Received message : " + message);
        StatusUpdater statusUpdater = new StatusUpdater(context);
        statusUpdater.updateStatus("Received message : " + message);
        if (isDisconnectMessage(message)) {
            disconnectFromServer();
        } else {
            if (isVolumeMessage(message)) {
                VolumeUpdater volumeUpdater = new VolumeUpdater(context);
                volumeUpdater.updateVolume(getVolumeFromMessage(message));
            }
        }
    }

    private void disconnectFromServer() {
        context.stopService(RVCServiceFactory.stopService(context));
    }

    private boolean isDisconnectMessage(String message) {
        return message.substring(0, 1).equals(SERVER_DISCONNECT);
    }

    private boolean isVolumeMessage(String message) {
        try {
            getVolumeFromMessage(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private int getVolumeFromMessage(String message) {
        return Integer.parseInt(message.substring(2, 4), HEX_RADIX);
    }

}

