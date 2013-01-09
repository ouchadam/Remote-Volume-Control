package com.rvc.server;

import com.rvc.util.ConnectionTimeout;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements SocketReader.ReceiverCallback, ConnectionTimeout {

    private static final String UPDATE_SERVER_START = "Server start";

    private final ConnectionState connectionState;
    private final SocketHandler socketHandler;

    private IOController ioController;
    private ServerCallbacks callback;
    private int port;

    public Server(ServerSettings serverSettings) {
        port = serverSettings.getPort();
        connectionState = new ConnectionState();
        socketHandler = new SocketHandler(callback);
        ioController = new IOHandler(clientSocket);
    }

    public void setCallback(ServerCallbacks callback) {
        this.callback = callback;
    }

    public boolean startServer() throws IOException {
        updateStatus(UPDATE_SERVER_START);
        initConnection();
        new SocketReader(ioController, connectionState, this).start().join();
        callback.onClientDisconnected();
        closeConnection();
        return isServerToBeRestarted();
    }

    private void initConnection() throws IOException {
        initSockets();
        initIO();
    }

    private void initSockets() throws IOException {

    }

    private void initIO() throws IOException {
        ioController = new IOHandler(clientSocket);
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
            closeSockets();
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
        }
        connectionState.setServerQuit();
        closeConnection();
    }

    private boolean isOutputOpen() {
        return ioController.printWriter() != null;
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
