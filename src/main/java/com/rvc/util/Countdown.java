package com.rvc.util;

import com.rvc.server.ConnectionState;

public class Countdown implements Runnable {

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
            for (int i = 10; i > 0; i--) {
                if (connectionState.getClientConnected()) {
                    timeoutCallback.onConnected();
                    break;
                } else {
                    timeoutCallback.onCountdown(i);
                    Thread.sleep(1000);
                }
            }
            if (connectionState.getServerRunning()) {

                if (!connectionState.getClientConnected()) {
                    timeoutCallback.onConnectionTimedOut();
                }
            }

        } catch (InterruptedException e) {
            //e.printStackTrace();
//            setError("Error : Timeout IE");
        }
    }
}
