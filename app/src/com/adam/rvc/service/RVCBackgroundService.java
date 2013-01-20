package com.adam.rvc.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.adam.rvc.R;
import com.adam.rvc.receiver.OnMessageReceived;
import com.adam.rvc.receiver.ReceiverIntentFactory;
import com.adam.rvc.util.Log;
import com.adam.rvc.util.StatusUpdater;

import java.io.IOException;

public class RVCBackgroundService extends Service implements OnMessageReceived {

    public static final String ACTION_START = "com.adam.rvc.service.rvcbackgroundservice.ACTION_START";
    public static final String ACTION_WRITE = "com.adam.rvc.service.rvcbackgroundservice.ACTION_WRTE";
    public static final String EXTRA_IP_ADDRESS = "com.adam.rvc.service.rvcbackgroundservice.EXTRA_IP_ADDRESS";
    public static final String EXTRA_PORT_INT = "com.adam.rvc.service.rvcbackgroundservice.EXTRA_PORT_INT";
    public static final String EXTRA_WRITE_MESSAGE = "com.adam.rvc.service.rvcbackgroundservice.EXTRA_WRITE_MESSAGE";

    private static final int EXTRA_ERROR_VALUE = 0;

    private final OnMessageReceived onMessageReceived = this;
    private final StatusUpdater statusUpdater;
    private static final int MAX_RETRIES = 3;

    private ServerConnection connection;
    private int retries = 1;


    public RVCBackgroundService() {
        this(null);
    }

    public RVCBackgroundService(ServerConnection connection) {
        this.connection = connection;
        statusUpdater = new StatusUpdater(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        statusUpdater.updateStatusAndLog(getString(R.string.creating_service));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.log("Service started with intent : " + intent);
        if (intent.getAction().equals(ACTION_START)) {
            connect(intent.getStringExtra(EXTRA_IP_ADDRESS), intent.getIntExtra(EXTRA_PORT_INT, EXTRA_ERROR_VALUE));
        } else if (intent.getAction().equals(ACTION_WRITE)) {
            writeMessageToServer(intent.getStringExtra(EXTRA_WRITE_MESSAGE));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void connect(final String ipAddress, final int port) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (connection == null) {
                        connection = new RVCConnection(ipAddress, port, onMessageReceived, statusUpdater);
                    }
                    connection.connect();
                } catch (IOException e) {
                    connection = null;
                    statusUpdater.updateStatusAndLog(getString(R.string.connection_failed), e);
                    e.printStackTrace();
                    if (retries < MAX_RETRIES) {
                        retryConnection(ipAddress, port);
                    }
                }
            }
        }).start();

    }

    private void retryConnection(String ipAddress, int port) {
        try {
            Thread.sleep(1000);
            statusUpdater.updateStatus("Retrying : " + retries);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        retries ++;
        connect(ipAddress, port);
    }

    private void writeMessageToServer(String message) {
        if (connection != null) {
            connection.write(message);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        statusUpdater.updateStatusAndLog(getString(R.string.destroying_service));
        stop();
    }

    void stop() {
        disconnectConnection();
    }

    private void disconnectConnection() {
        if (connection != null) {
            connection.write("exit");
            connection.disconnect();
            connection = null;
        }
    }

    @Override
    public void onMessageReceived(String message) {
        broadcastServerMessage(message);
    }

    private void broadcastServerMessage(String message) {
        sendBroadcast(ReceiverIntentFactory.broadcastServerMessage(message));
    }

}