package com.rvc.server;

public class ConnectionState {

    private boolean serverQuit;
    private boolean clientConnected;
    private boolean serverRunning;
    private boolean socketsOpened;

    public void setClientConnected(boolean clientConnected) {
        this.clientConnected = clientConnected;
    }

    public boolean getServerRunning() {
        return serverRunning;
    }

    public void setServerRunning(boolean serverRunning) {
        this.serverRunning = serverRunning;
    }

    public boolean getClientConnected() {
        return clientConnected;
    }

    public void setServerQuit(boolean state) {
        this.serverQuit = state;
    }

    public boolean getServerQuit() {
        return serverQuit;
    }

    public boolean getSocketsOpened() {
        return socketsOpened;
    }

    public void setSocketsOpened(boolean state) {
        socketsOpened = state;
    }
}
