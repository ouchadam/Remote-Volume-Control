package com.server.server;

import com.server.Discovery;
import com.server.DiscoverySettings;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class Server implements ConnectionHandler.Callback, Discovery.Callback {

    private static final String UPDATE_SERVER_START = "Server starting";

    private final ServerState serverState;
    private final Map<String, ConnectionHandler> readingThreads;
    private final Discovery discovery;

    private SocketHandler socketHandler;
    private ClientConnectionCallback connectionCallback;
    private final ServerSettings settings;
    private ClientMessageCallback clientMessageCallback;

    public Server(ServerSettings serverSettings) throws IOException {
        settings = serverSettings;
        discovery = new Discovery(createDiscoverySettings(settings), this);
        readingThreads = new HashMap<String, ConnectionHandler>();
        serverState = new ServerState();
    }

    private DiscoverySettings createDiscoverySettings(ServerSettings serverSettings) {
        return new DiscoverySettings(serverSettings, "rvc-service");
    }

    public void setConnectionCallback(ClientConnectionCallback callback) {
        this.connectionCallback = callback;
    }

    public void setMessageCallback(ClientMessageCallback callback) {
        this.clientMessageCallback = callback;
    }

    public void startServer() throws IOException {
        updateStatus(UPDATE_SERVER_START);
        startDiscovery();
        handleConnections();
    }

    private void startDiscovery() throws IOException {
        discovery.start(new Discovery.Failed() {
            @Override
            public void onDiscoveryFailed() {
                //Todo
            }
        });
    }

    private void updateStatus(String status) {
        connectionCallback.onStatusUpdate(status);
    }

    private void handleConnections() throws IOException {
        initConnection();
        clientConnectionLoop();
        closeServerConnection();
    }

    private void initConnection() throws IOException {
        initServerSocket();
        serverState.setServerRunning(true);
    }

    private void initServerSocket() throws IOException {
        socketHandler = new SocketHandler(connectionCallback);
        socketHandler.initServerSocket(settings.getPort());
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

    public void quit() throws IOException {
        discovery.finish();
        serverState.setServerRunning(false);

        Iterator it = readingThreads.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            ((ConnectionHandler) pairs.getValue()).stop();
            it.remove();
        }
    }

    @Override
    public void clientThreadEstablished(ConnectionHandler connectionHandler) {
        readingThreads.put(connectionHandler.getClientName(), connectionHandler);
        connectionCallback.onClientConnected(connectionHandler.getClientName());
    }

    @Override
    public void clientThreadFinished(ConnectionHandler connectionHandler) {
        readingThreads.remove(connectionHandler);
        connectionCallback.onClientDisconnected(connectionHandler.getClientName());
    }

    @Override
    public void clientMessageReceived(String clientName, String message) {
        clientMessageCallback.onMessageReceived(clientName, message);
        printResponse(clientName);
    }

    private void printResponse(String clientName) {
        readingThreads.get(clientName).writeToClient(clientMessageCallback.replyToClient());
    }

    @Override
    public void onDiscoveryUpdate(String update) {
        connectionCallback.onDiscoveryStatusUpdate(update);
    }

}
