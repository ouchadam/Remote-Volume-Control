package com.rvc.server;

import com.rvc.util.ConnectionTimeout;

import java.io.IOException;

public class Server implements SocketReader.ReceiverCallback, ConnectionTimeout {

    private static final String UPDATE_SERVER_START = "Server starting";

    private final ConnectionState connectionState;
    private SocketHandler socketHandler;

    private IOController ioController;
    private ServerCallbacks callback;
    private int port;

    public Server(ServerSettings serverSettings) {
        port = serverSettings.getPort();
        connectionState = new ConnectionState();
    }

    public void setCallback(ServerCallbacks callback) {
        this.callback = callback;
    }

    public boolean startServer() throws IOException {
        updateStatus(UPDATE_SERVER_START);
        initConnection();
        callback.onClientConnected();
        startSocketReadingThread();
        closeConnection();
        callback.onClientDisconnected();
        return isServerToBeRestarted();
    }

    private void startSocketReadingThread() {
        new SocketReader(ioController, connectionState, this).start().join();
    }

    private void initConnection() throws IOException {
        initSockets();
        initIO();
    }

    private void initSockets() throws IOException {
        socketHandler = new SocketHandler(callback, connectionState);
        socketHandler.createSockets(port);
    }

    private void initIO() throws IOException {
        ioController = new IOHandler(socketHandler);
        ioController.openIO();
    }

    @Override
    public void onReceiveMessage(String message) {
        if (isValidMessage(message)) {
            sendMessage(message);
        }
    }

    private boolean isValidMessage(String message) {
        return message.substring(1, 2).equals("1");
    }

    private void sendMessage(String message) {
        ioController.printWriter().println(message);
    }

    private void closeConnection() {
        try {
            ioController.closeIO();
            socketHandler.closeSockets();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateError(String error) {
        System.out.println(error);
        callback.onErrorUpdate(error);
    }

    public void quit() {
        if (isOutputOpen()) {
            sendDisconnectPacket();
            closeConnection();
        }
        connectionState.setServerQuit();
    }

    private boolean isOutputOpen() {
        return ioController != null;
    }

    private void sendDisconnectPacket() {
        sendMessage("1000");
    }

    private boolean isServerToBeRestarted() {
        return !connectionState.getServerQuit();
    }

    private void updateStatus(String status) {
        System.out.println(status);
        callback.onStatusUpdate(status);
    }

    @Override
    public void onCountdown(int value) {
        updateStatus("Timeout : " + value);
    }

    @Override
    public void onConnectionTimedOut() {
        updateStatus("Client Timed Out");
        connectionState.setServerRunning(false);
        connectionState.setClientConnected(false);
    }

    @Override
    public void onConnected() {
        updateStatus("Client Connected");
        callback.onClientConnected();
    }

}
