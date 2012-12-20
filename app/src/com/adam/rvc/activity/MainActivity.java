package com.adam.rvc.activity;

import android.os.Bundle;
import android.widget.TextView;
import com.adam.rvc.R;
import com.adam.rvc.receiver.ServerMessageReceiver;
import com.adam.rvc.receiver.StatusUpdateReceiver;
import com.adam.rvc.service.RVCServiceFactory;
import com.adam.rvc.util.MessageHandler;

public class MainActivity extends RVCActivity implements StatusUpdateReceiver.OnStatusUpdated {

    private static final String IP = "192.168.0.7";
    private static final int  PORT = 5555;

    private final StatusUpdateReceiver statusReceiver;
    private final ServerMessageReceiver messageReceiver;

    private TextView statusText;

    public MainActivity() {
        statusReceiver = new StatusUpdateReceiver(this);
        messageReceiver = new ServerMessageReceiver();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initMessageReceiver();
        initViews();
    }

    private void initMessageReceiver() {
        messageReceiver.setMessageHandler(new MessageHandler(this));
    }

    private void initViews() {
        initText();
    }

    private void initText() {
        statusText = (TextView) findViewById(R.id.status_text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(RVCServiceFactory.startService(this, IP, PORT));
        registerReceivers();
    }

    private void registerReceivers() {
        registerReceiver(statusReceiver, statusReceiver.getIntentFilter());
        registerReceiver(messageReceiver, messageReceiver.getIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(RVCServiceFactory.stopPushService(this));
        unregisterReceivers();
    }

    private void unregisterReceivers() {
        unregisterReceiver(statusReceiver);
        unregisterReceiver(messageReceiver);
    }

    @Override
    public void onStatusUpdate(String message) {
        statusText.setText(message);
    }
}

