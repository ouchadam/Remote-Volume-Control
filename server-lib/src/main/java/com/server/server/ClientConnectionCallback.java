package com.server.server;

public interface ClientConnectionCallback {

    void onStatusUpdate(String update);

    void onDiscoveryStatusUpdate(String update);

    void onClientConnected(String client);

    void onClientDisconnected(String client);

}
