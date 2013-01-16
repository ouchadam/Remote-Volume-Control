package com.adam.rvc.discovery;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.adam.rvc.util.StatusUpdater;

import java.io.IOException;

public class DiscoveryService extends Service {

    private final StatusUpdater statusUpdater;
    private ServerDiscoverer serverDiscoverer;

    public DiscoveryService() {
        statusUpdater = new StatusUpdater(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        serverDiscoverer = new ServerDiscoverer(this, statusUpdater);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            serverDiscoverer.startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serverDiscoverer.onDestroy();
    }

}
