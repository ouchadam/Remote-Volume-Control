package com.server;

import com.server.server.ClientMessageCallback;
import com.server.server.Protocol;

public class ServerMessageHandler implements ClientMessageCallback {

    private final Protocol protocol;
    private String reply;

    public ServerMessageHandler() {
        protocol = new Protocol();
    }

    @Override
    public void onMessageReceived(String clientName, String message) {
        reply = protocol.processInput(message);
    }

    @Override
    public String writeToClient() {
        return reply;
    }

}
