package com.adam.rvc.listener;

import com.adam.rvc.util.StatusUpdater;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

public class DiscoveryListener implements ServiceListener {

    private final StatusUpdater statusUpdater;
    private final DiscoveryCallback callback;

    public DiscoveryListener(StatusUpdater statusUpdater, DiscoveryCallback callback) {
        this.statusUpdater = statusUpdater;
        this.callback = callback;
    }

    @Override
    public void serviceAdded(ServiceEvent serviceEvent) {
        statusUpdater.updateStatusAndLog("Service added");
        callback.resolve(serviceEvent);
    }

    @Override
    public void serviceRemoved(ServiceEvent serviceEvent) {
        statusUpdater.updateStatusAndLog("Service removed");
    }

    @Override
    public void serviceResolved(ServiceEvent serviceEvent) {
        statusUpdater.updateStatusAndLog("Service resolved");
        callback.onServerDataReceived(serviceEvent);
    }

    public interface DiscoveryCallback {

        void resolve(ServiceEvent serviceEvent);

        void onServerDataReceived(ServiceEvent serviceEvent);

    }
}
