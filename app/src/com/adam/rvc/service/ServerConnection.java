package com.adam.rvc.service;

interface ServerConnection {

    void connect();

    void disconnect();

    void write(String message);

}
