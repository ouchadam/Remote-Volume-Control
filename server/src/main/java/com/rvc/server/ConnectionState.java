package com.rvc.server;

public class ConnectionState {

    private boolean serverQuit;
    private boolean clientConnected;
    private boolean serverRunning;

    public void setClientConnected(boolean clientConnected) {
        this.clientConnected = clientConnected;
    }

    public boolean isServerRunning() {
        return serverRunning;
    }

    public void setServerRunning(boolean serverRunning) {
        this.serverRunning = serverRunning;
    }

    public boolean isClientConnected() {
        return clientConnected;
    }

    public void setServerQuit() {
        this.serverQuit = true;
        serverRunning = false;
    }

    public boolean getServerQuit() {
        return serverQuit;
    }

}
