package com.server.server;

public interface ClientMessageCallback {

    void onMessageReceived(String clientName, String message);

    String writeToClient();

}
