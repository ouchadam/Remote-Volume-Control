package com.rvc.util;

import com.rvc.server.ConnectionState;

public class Countdown implements Runnable {

    private static final int TOTAL_RETRY_ATTEMPTS = 10;
    private static final int RETRY_SLEEP_IN_MS = 1000;

    private final ConnectionTimeout timeoutCallback;
    private final ConnectionState connectionState;

    public Countdown(ConnectionTimeout timeoutCallback, ConnectionState connectionState) {
        this.timeoutCallback = timeoutCallback;
        this.connectionState = connectionState;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            if (!timeoutRetryLoop()) {
                connectionTimedOut();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean timeoutRetryLoop() throws InterruptedException {
        for (int i = TOTAL_RETRY_ATTEMPTS; i > 0; i--) {
            if (!connectionState.isServerRunning()) {
                return false;
            }
            if (connectionState.isClientConnected()) {
                connectionConnected();
                return true;
            } else {
                timeoutCallback.onCountdown(i);
                Thread.sleep(RETRY_SLEEP_IN_MS);
            }
        }
        return false;
    }

    private void connectionConnected() {
        timeoutCallback.onConnected();
    }

    private void connectionTimedOut() {
        timeoutCallback.onConnectionTimedOut();
    }

}
