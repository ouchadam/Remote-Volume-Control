package com.adam.rvc.service;

public interface ServerConnection {

    void connect();

    void disconnect();

    void write(String message);

}
