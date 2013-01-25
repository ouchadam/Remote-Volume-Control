package com.server.server;

import java.io.BufferedReader;
import java.io.IOException;

class SocketReader implements Runnable {

    private static final String SERVER_EXIT = "exit";

    private final Thread readClientResponse;
    private final BufferedReader in;
    private final ServerState serverState;
    private final Callback callback;
    private static final String ERROR = "error";

    public interface Callback {
        void onReceiveMessage(String message);
        void onConnectionFinished();
    }

    public SocketReader(BufferedReader bufferedReader, ServerState serverState, Callback callback) {
        this.in = bufferedReader;
        this.serverState = serverState;
        this.callback = callback;
        readClientResponse = new Thread(this);
    }

    public SocketReader start() {
        readClientResponse.start();
        return this;
    }

    @Override
    public void run() {
        boolean shouldRun = true;
        while (shouldRun && serverState.isServerRunning()) {
            String rawMessage = getRawMessage();
            messageCallback(rawMessage);
            if (rawMessage.equals(SERVER_EXIT)) {
                shouldRun = false;
            }
        }
        callback.onConnectionFinished();
    }

    private void messageCallback(String rawMessage) {
        if (!rawMessage.equals(ERROR)) {
            callback.onReceiveMessage(rawMessage);
        }
    }

    private String getRawMessage() {
        try {
            String rawMessage = in.readLine();
            if (rawMessage != null) {
                return rawMessage;
            }
        } catch (IOException e) {
            serverState.setServerRunning(false);
        }
        return ERROR;
    }

}
