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
    private final SharedPrefsHelper sharedPrefsHelper;
    private final VolumeUpdater volumeUpdater;

    public MessageHandler(Context context) {
        this(context, new SharedPrefsHelper(context), new VolumeUpdater(context));
    }

    public MessageHandler(Context context, SharedPrefsHelper sharedPrefsHelper, VolumeUpdater volumeUpdater) {
        this.context = context;
        this.sharedPrefsHelper = sharedPrefsHelper;
        this.volumeUpdater = volumeUpdater;
    }

    @Override
    public void onMessageReceived(String message) {
        if (isValidMessage(message)) {
            if (showMessageData()) {
                updateStatusWithMessage(message);
            }
            if (isDisconnectMessage(message)) {
                disconnectFromServer();
            } else {
                if (isValidVolumeMessage(message)) {
                    volumeUpdater.updateVolume(getVolumeFromMessage(message));
                }
            }
        }
    }

    private boolean isValidMessage(String message) {
        return message != null && message.length() > 0;
    }

    private boolean showMessageData() {
        return sharedPrefsHelper.getShowMessageSetting();
    }

    private void updateStatusWithMessage(String message) {
        StatusUpdater statusUpdater = new StatusUpdater(context);
        statusUpdater.updateStatus("Received message : " + message);
    }

    private void disconnectFromServer() {
        context.stopService(RVCServiceFactory.stopService(context));
    }

    private boolean isDisconnectMessage(String message) {
        return message.substring(0, 1).equals(SERVER_DISCONNECT);
    }

    private boolean isValidVolumeMessage(String message) {
        try {
            return new VolumeValidater().validate(getVolumeFromMessage(message));
        } catch (NumberFormatException e) {
            return false;
        } catch (StringIndexOutOfBoundsException e) {
            return false;
        }
    }

    private int getVolumeFromMessage(String message) {
        return Integer.parseInt(message.substring(2, 4), HEX_RADIX);
    }

}

