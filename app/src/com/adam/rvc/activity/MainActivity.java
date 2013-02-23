package com.adam.rvc.activity;

import android.os.Bundle;
import android.view.View;
import com.adam.rvc.R;
import com.adam.rvc.receiver.ServerMessageReceiver;
import com.adam.rvc.service.RVCServiceFactory;
import com.adam.rvc.util.MessageHandler;
import com.adam.rvc.util.SharedPrefsHelper;

public class MainActivity extends AbstractRVCActivity {

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
        findViewById(R.id.server_discovery_fragment).setVisibility(getFragmentVisibility());
        startService(RVCServiceFactory.startServerScanner(this));
        registerReceiver();
    }

    private int getFragmentVisibility() {
        return new SharedPrefsHelper(this).getShowServerDetailsSetting() ? View.VISIBLE : View.GONE;
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

