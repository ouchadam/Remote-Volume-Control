package com.adam.rvc;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import com.adam.rvc.receiver.StatusUpdateReceiver;
import com.adam.rvc.service.RVCServiceFactory;
import com.adam.rvc.util.StatusUpdater;

public class MainActivity extends Activity implements StatusUpdateReceiver.OnStatusUpdated {

    private static final String IP = "192.168.0.7";
    private static final int  PORT = 5555;

    private TextView statusText;
    private StatusUpdateReceiver statusReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        statusText = (TextView) findViewById(R.id.status_text);
        statusReceiver = new StatusUpdateReceiver(this);
        initSeekBar();
    }

    private void initSeekBar() {
        SeekBar seekBar = (SeekBar) findViewById(R.id.volume_seek_bar);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBarListener(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(RVCServiceFactory.startService(this, IP, PORT));
        registerReceiver(statusReceiver, createStatusUpdateFilter());
    }

    private IntentFilter createStatusUpdateFilter() {
        return new IntentFilter(StatusUpdater.ACTION_STATUS_MESSAGE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(RVCServiceFactory.stopPushService(this));
        unregisterReceiver(statusReceiver);
    }

    @Override
    public void onStatusUpdate(String message) {
        statusText.setText(message);
    }
}

