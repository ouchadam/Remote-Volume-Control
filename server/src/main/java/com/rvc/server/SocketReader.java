package com.rvc.server;

import com.rvc.util.ExceptionLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

class SocketReader implements Runnable {

    private static final String SERVER_EXIT = "1";

    private final Thread readClientResponse;
    private final BufferedReader in;
    private final ServerState serverState;
    private final ReceiverCallback receiverCallback;

    private String message;

    public SocketReader(BufferedReader bufferedReader, ServerState serverState, ReceiverCallback receiverCallback) {
        this.in = bufferedReader;
        this.serverState = serverState;
        this.receiverCallback = receiverCallback;
        readClientResponse = new Thread(this);
    }

    public SocketReader start() {
        readClientResponse.start();
        return this;
    }

    @Override
    public void run() {
        Protocol protocol = new Protocol(serverState);
        boolean shouldRun = true;

        while (shouldRun && serverState.isServerRunning()) {
            try {
                String rawMessage = in.readLine();
                if (rawMessage != null) {
                    message = protocol.processInput(rawMessage);
                    receiverCallback.onReceiveMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (e instanceof SocketException) {
                    serverState.setServerRunning(false);
                } else {
                    ExceptionLogger.saveException(e);
                    serverState.setServerRunning(false);
                }
            }

            if (message.substring(0, 1).equals(SERVER_EXIT)) {
                shouldRun = false;
            }
        }
        try {
            receiverCallback.onConnectionFinished();
        } catch (IOException e) {
        }
    }

    public interface ReceiverCallback {

        void onReceiveMessage(String message);

        void onConnectionFinished() throws IOException;

    }

}
