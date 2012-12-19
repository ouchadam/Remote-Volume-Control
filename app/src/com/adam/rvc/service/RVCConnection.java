package com.adam.rvc.service;

import com.adam.rvc.util.Log;
import com.adam.rvc.util.StatusUpdater;

import java.io.IOException;

public class RVCConnection implements ServerConnection, RVCClient.OnMessageReceived {

    private final RVCClient client;
    private final StatusUpdater statusUpdater;
    private final OnVolumeUpdate onVolumeUpdate;

    public RVCConnection(String ip, int port, OnVolumeUpdate onVolumeUpdate, StatusUpdater statusUpdater) throws IOException {
        this.statusUpdater = statusUpdater;
        this.onVolumeUpdate = onVolumeUpdate;
        client = new RVCClient(ip, port);
        client.setMessageListener(this);
    }

    @Override
    public void connect() {
        statusUpdater.updateStatusAndLog("Connecting...");
        client.connect();
    }

    @Override
    public void write(String message) {
        client.writeToServer(message);
    }

    @Override
    public void disconnect() {
        statusUpdater.updateStatus("Disconnected");
        client.disconnect();
    }

    @Override
    public void OnMessageReceived(String message) {
        Log.log("Packet : " + message);
        if (isDisconnectMessage(message)) {
            disconnect();
            return;
        }
        onVolumeUpdate.onVolumeUpdate(getVolumeFromMessage(message));
    }

    private boolean isDisconnectMessage(String message) {
        return message.substring(0, 1).equals("1");
    }

    private int getVolumeFromMessage(String message) {
        return Integer.parseInt(message.substring(2, 4), 16);
    }

    public interface OnVolumeUpdate {

        void onVolumeUpdate(int volume);

    }
}
