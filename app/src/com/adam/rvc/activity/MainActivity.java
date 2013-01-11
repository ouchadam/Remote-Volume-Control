package com.adam.rvc.activity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.adam.rvc.R;
import com.adam.rvc.receiver.ServerMessageReceiver;
import com.adam.rvc.service.RVCServiceFactory;
import com.adam.rvc.util.MessageHandler;

public class MainActivity extends RVCActivity  {

    private final ServerMessageReceiver messageReceiver;

    public MainActivity() {
        messageReceiver = new ServerMessageReceiver();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(R.layout.activity_main);
        initMessageReceiver();
    }

    private void initWindow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    private void initMessageReceiver() {
        messageReceiver.setMessageHandler(new MessageHandler(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(RVCServiceFactory.startServerScanner(this));
        registerReceiver();
    }

    private void registerReceiver() {
        registerReceiver(messageReceiver, messageReceiver.getIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(RVCServiceFactory.stopService(this));
        unregisterReceiver();
    }

    private void unregisterReceiver() {
        unregisterReceiver(messageReceiver);
    }

}

