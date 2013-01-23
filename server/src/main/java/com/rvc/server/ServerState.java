package com.rvc.server;

public class ServerState {

    private boolean serverQuit;
    private boolean clientConnected;
    private boolean serverRunning;

    public void setClientConnected(boolean clientConnected) {
        this.clientConnected = clientConnected;
    }

    public synchronized boolean isServerRunning() {
        return serverRunning;
    }

    public synchronized void setServerRunning(boolean serverRunning) {
        this.serverRunning = serverRunning;
    }

    public boolean isClientConnected() {
        return clientConnected;
    }

    public void setServerQuit() {
        serverRunning = false;
    }

    public boolean getServerQuit() {
        return serverQuit;
    }

}
