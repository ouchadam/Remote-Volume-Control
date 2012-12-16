package com.rvc.server;

public interface ServerCallbacks {

    void onStatusUpdate(String update);

    void onErrorUpdate(String update);

    void onClientConnected();

    void onClientDisconnected();

}
