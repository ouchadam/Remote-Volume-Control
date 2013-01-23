package com.rvc.server;

public interface ServerCallbacks {

    void onStatusUpdate(String update);

    void onDiscoveryStatusUpdate(String update);

    void onClientConnected();

    void onClientDisconnected();

}
