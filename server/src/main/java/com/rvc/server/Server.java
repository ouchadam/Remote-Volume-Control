package com.rvc.server;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class Server implements ConnectionHandler.ConnectionCallbacks {

    private static final String UPDATE_SERVER_START = "Server starting";

    private final ServerState serverState;
    private final List<ConnectionHandler> readingThreads = new ArrayList<ConnectionHandler>();

    private SocketHandler socketHandler;
    private ServerCallbacks callback;
    private int port;

    public Server(ServerSettings serverSettings) {
        port = serverSettings.getPort();
        serverState = new ServerState();
    }

    public void setGuiCallbacks(ServerCallbacks callback) {
        this.callback = callback;
    }

    public boolean startServer() throws IOException {
        updateStatus(UPDATE_SERVER_START);
        handleConnections();
        return isServerToBeRestarted();
    }

    private void updateStatus(String status) {
        callback.onStatusUpdate(status);
    }

    private void handleConnections() throws IOException {
        initConnection();
        serverState.setServerRunning(true);
        clientConnectionLoop();
        closeServerConnection();
    }

    private void initConnection() throws IOException {
        initServerSocket();
    }

    private void initServerSocket() throws IOException {
        socketHandler = new SocketHandler(callback);
        socketHandler.initServerSocket(port);
    }

    private void clientConnectionLoop() throws IOException {
        while (serverState.isServerRunning()) {
            Socket client = waitForNewClient();
            startNewConnectionThread(client);
        }
    }

    private Socket waitForNewClient() throws IOException {
        return socketHandler.waitForClient();
    }

    private void startNewConnectionThread(final Socket clientSocket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    handleNewConnection(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleNewConnection(Socket clientSocket) throws IOException {
        ConnectionHandler connectionHandler = new ConnectionHandler(this, serverState);
        connectionHandler.handleNewConnection(clientSocket);
    }

    private void closeServerConnection() {
        try {
            socketHandler.closeServerSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isServerToBeRestarted() {
        return serverState.getServerQuit();
    }

    public void quit() {
        serverState.setServerRunning(false);
        for (ConnectionHandler socketReader : readingThreads) {
            socketReader.stop();
        }
    }

    @Override
    public void clientThreadEstablished(ConnectionHandler connectionHandler) {
        readingThreads.add(connectionHandler);
        callback.onClientConnected();
    }

    @Override
    public void clientThreadFinished(ConnectionHandler connectionHandler) {
        readingThreads.remove(connectionHandler);
        callback.onClientDisconnected();
    }

}
