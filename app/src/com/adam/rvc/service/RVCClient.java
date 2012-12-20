package com.adam.rvc.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RVCClient {

    private static final String CHECK = "check";
    private static final String CONNECTION = "conn";
    private static final String UPDATE_VOLUME = "uvol";

    private final Socket socket;
    private final PrintWriter output;
    private final BufferedReader input;

    private OnMessageReceived onMessageReceived;

    public RVCClient(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        output = new PrintWriter(socket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void setMessageListener(OnMessageReceived onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    public void connect() {
        if (waitForSocketConnection()) {
            sendConnectPacket();
            readVolumeFromServer();
        }
    }

    private boolean waitForSocketConnection() {
        for (int i = 0; i < 100; i ++) {
            if (socket.isConnected()) {
                return true;
            } else {
                sleepThread();
            }
        }
        return false;
    }

    private void sendConnectPacket() {
        writeToServer(CONNECTION);
        writeToServer(UPDATE_VOLUME);
    }

    private void sleepThread() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void readVolumeFromServer() {
        if (socket.isConnected()) {
            output.println(CHECK);
            try {
                onMessageReceived.OnMessageReceived(input.readLine());
            } catch (Exception e) {
                closeSocket();
            }
        }
    }

    public void writeToServer(String message) {
        if (socket.isConnected()) {
            output.println(message);
        }
    }

    public void disconnect() {
        closeSocket();
    }

    private void closeSocket() {
        if (socket.isConnected()) {
            try {
                output.flush();
                input.close();
                output.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnMessageReceived {

        void OnMessageReceived(String message);

    }

}
