package com.adam.rvc.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.adam.rvc.receiver.ReceiverIntentFactory;
import com.adam.rvc.util.Log;
import com.adam.rvc.util.StatusUpdater;

import java.io.IOException;

public class RVCBackgroundService extends Service implements RVCClient.OnMessageReceived {

    public static final String ACTION_START = "com.adam.rvc.server.pushservice.ACTION_START";
    public static final String ACTION_WRITE = "com.adam.rvc.server.pushservice.ACTION_WRTE";
    public static final String EXTRA_IP_ADDRESS = "com.adam.rvc.service.pushservice.EXTRA_IP_ADDRESS";
    public static final String EXTRA_PORT_INT = "com.adam.rvc.service.pushservice.EXTRA_PORT_INT";
    public static final String EXTRA_WRITE_MESSAGE = "com.adam.rvc.service.pushservice.EXTRA_WRITE_MESSAGE";

    private final RVCClient.OnMessageReceived onMessageReceived = this;

    private final StatusUpdater statusUpdater;
    private ServerConnection mConnection;

    public RVCBackgroundService() {
        statusUpdater = new StatusUpdater(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        statusUpdater.updateStatusAndLog("Creating service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.log("Service started with intent=" + intent);
        if (intent.getAction().equals(ACTION_START)) {
            connect(intent.getStringExtra(EXTRA_IP_ADDRESS), intent.getIntExtra(EXTRA_PORT_INT, 0));
        } else if (intent.getAction().equals(ACTION_WRITE)) {
            if (mConnection != null) {
                mConnection.write(intent.getStringExtra(EXTRA_WRITE_MESSAGE));
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void connect(final String ipAddress, final int port) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    mConnection = new RVCConnection(ipAddress, port, onMessageReceived, statusUpdater);
                    mConnection.connect();
                } catch (IOException e) {
                    mConnection = null;
                    statusUpdater.updateStatusAndLog("Connection Failed", e);
                }
            }
        }).start();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        statusUpdater.updateStatusAndLog("Service destroyed");
        stop();
    }

    void stop() {
        disconnectConnection();
    }

    private void disconnectConnection() {
        if (mConnection != null) {
            mConnection.write("exit");
            mConnection.disconnect();
            mConnection = null;
        }
    }

    @Override
    public void OnMessageReceived(String message) {
        broadcastServerMessage(message);
    }

    private void broadcastServerMessage(String message) {
        sendBroadcast(ReceiverIntentFactory.broadcastServerMessage(message));
    }

}