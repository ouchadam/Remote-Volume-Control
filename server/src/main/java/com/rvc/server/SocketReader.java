package com.rvc.server;

import com.rvc.util.ConnectionTimeout;
import com.rvc.util.ExceptionLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

class SocketReader implements Runnable {

    private static final String SERVER_EXIT = "1";

    private final Thread readClientResponse;
    private final BufferedReader in;
    private final ConnectionState connectionState;
    private final ReceiverCallback receiverCallback;
    private final ConnectionTimeout timeoutCallback;

    private String message;

    public SocketReader(IOController ioController, ConnectionState connectionState, Server serverCallbacks) {
        this.in = ioController.bufferReader();
        this.connectionState = connectionState;
        this.receiverCallback = serverCallbacks;
        this.timeoutCallback = serverCallbacks;
        readClientResponse = new Thread(this);
    }

    public SocketReader start() {
        readClientResponse.start();
        return this;
    }

    @Override
    public void run() {
//        new Countdown(timeoutCallback, connectionState);
        Protocol protocol = new Protocol(connectionState);

        while (connectionState.isServerRunning()) {
            try {
                String rawMessage = in.readLine();
                if (rawMessage != null) {
                    message = protocol.processInput(rawMessage);
                    receiverCallback.onReceiveMessage(message);
                }
            } catch (IOException e) {
                if (e instanceof SocketException) {
                    connectionState.setServerRunning(false);
                } else {
                    ExceptionLogger.saveException(e);
                    connectionState.setServerRunning(false);
                }
            }

            if (message.substring(0, 1).equals(SERVER_EXIT)) {
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
