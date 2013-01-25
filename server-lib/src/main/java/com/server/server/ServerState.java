package com.server.server;

public class ServerState {

    private boolean serverRunning;

    public synchronized boolean isServerRunning() {
        return serverRunning;
    }

    public synchronized void setServerRunning(boolean serverRunning) {
        this.serverRunning = serverRunning;
    }

}
