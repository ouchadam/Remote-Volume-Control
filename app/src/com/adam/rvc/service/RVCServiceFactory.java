package com.adam.rvc.service;

import android.content.Context;
import android.content.Intent;

public class RVCServiceFactory {

    public static Intent stopService(Context context) {
        return startService(context, "", 0);
    }

    public static Intent startService(Context context, String ipAddress, int port) {
        Intent pushServiceIntent = new Intent(context, RVCBackgroundService.class);
        pushServiceIntent.setAction(RVCBackgroundService.ACTION_START);
        pushServiceIntent.putExtra(RVCBackgroundService.EXTRA_IP_ADDRESS, ipAddress);
        pushServiceIntent.putExtra(RVCBackgroundService.EXTRA_PORT_INT, port);
        return pushServiceIntent;
    }

    public static Intent writeToServer(Context context, String message) {
        Intent pushServiceIntent = new Intent(context, RVCBackgroundService.class);
        pushServiceIntent.setAction(RVCBackgroundService.ACTION_WRITE);
        pushServiceIntent.putExtra(RVCBackgroundService.EXTRA_WRITE_MESSAGE, message);
        return pushServiceIntent;
    }
}
