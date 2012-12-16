package com.rvc.util;

public interface ConnectionTimeout {

    void onCountdown(int value);

    void onConnectionTimedOut();

    void onConnected();

}
