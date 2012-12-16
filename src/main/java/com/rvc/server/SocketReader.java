package com.rvc.server;

import com.rvc.util.ExeptionLogger;

import java.io.BufferedReader;

class SocketReader implements Runnable {

    private final Thread readClientResponse;
    private final BufferedReader in;
    private final ConnectionState connectionState;
    private final ReceiverCallback receiverCallback;

    private String message;

    public SocketReader(BufferedReader in, ConnectionState connectionState, ReceiverCallback receiverCallback) {
        this.in = in;
        this.connectionState = connectionState;
        this.receiverCallback = receiverCallback;
        readClientResponse = new Thread(this);
        readClientResponse.start();
    }

    @Override
    public void run() {

        Protocol protocol = new Protocol(connectionState);

        while (connectionState.isServerRunning()) {
            if (connectionState.isSocketsOpened()) {
                try {
                    String rawMessage = in.readLine();
                    if (rawMessage != null) {
                        message = protocol.processInput(rawMessage);
                        receiverCallback.onReceiveMessage(message);
                    }
                } catch (Exception e) {
                    ExeptionLogger.saveException(e);
                    connectionState.setServerRunning(false);
                }
            }

            if (message.substring(0, 1).equals("1")) {
                connectionState.setServerRunning(false);
            }
        }

    }

    public void join() {
        try {
            this.readClientResponse.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public interface ReceiverCallback {

        void onReceiveMessage(String message);

    }
}
