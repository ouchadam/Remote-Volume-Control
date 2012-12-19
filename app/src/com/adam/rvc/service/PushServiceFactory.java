package com.adam.rvc.service;

import android.content.Context;
import android.content.Intent;

public class PushServiceFactory {

    public static Intent stopPushService(Context context) {
        return startPushService(context, "", 0);
    }

    public static Intent startPushService(Context context, String ipAddress, int port) {
        Intent pushServiceIntent = new Intent(context, PushService.class);
        pushServiceIntent.setAction(PushService.ACTION_START);
        pushServiceIntent.putExtra(PushService.EXTRA_IP_ADDRESS, ipAddress);
        pushServiceIntent.putExtra(PushService.EXTRA_PORT_INT, port);
        return pushServiceIntent;
    }

    public static Intent writeToServer(Context context, String message) {
        Intent pushServiceIntent = new Intent(context, PushService.class);
        pushServiceIntent.setAction(PushService.ACTION_WRITE);
        pushServiceIntent.putExtra(PushService.EXTRA_WRITE_MESSAGE, message);
        return pushServiceIntent;
    }
}
