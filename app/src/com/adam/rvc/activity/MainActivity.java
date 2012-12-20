package com.adam.rvc.activity;

import android.os.Bundle;
import com.adam.rvc.R;
import com.adam.rvc.receiver.ServerMessageReceiver;
import com.adam.rvc.service.RVCServiceFactory;
import com.adam.rvc.util.MessageHandler;

public class MainActivity extends RVCActivity  {

    private static final String IP = "192.168.0.7";
    private static final int  PORT = 5555;

    private final ServerMessageReceiver messageReceiver;

    public MainActivity() {
        messageReceiver = new ServerMessageReceiver();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMessageReceiver();
    }

    private void initMessageReceiver() {
        messageReceiver.setMessageHandler(new MessageHandler(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(RVCServiceFactory.startService(this, IP, PORT));
        registerReceiver();
    }

    private void registerReceiver() {
        registerReceiver(messageReceiver, messageReceiver.getIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(RVCServiceFactory.stopPushService(this));
        unregisterReceiver();
    }

    private void unregisterReceiver() {
        unregisterReceiver(messageReceiver);
    }

}

