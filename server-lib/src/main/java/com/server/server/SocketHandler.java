package com.server.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketHandler {

    private static final String UPDATE_WAITING_FOR_CLIENT = "Waiting for client";
    private static final String UPDATE_CLIENT_SOCKET_CONNECTED = "Client connected";
    private static final String ERROR_SERVER_SOCKET_IO_EXCEPTION = "couldn't connect : IOException || is the server already running?";

    private final ClientConnectionCallback callback;

    private ServerSocket serverSocket;

    public SocketHandler(ClientConnectionCallback callback) {
        this.callback = callback;
    }

    public void initServerSocket(int port) throws IOException {
        serverSocket = createServerSocket(port);
    }

    private ServerSocket createServerSocket(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println(ERROR_SERVER_SOCKET_IO_EXCEPTION);
            System.exit(1);
        }
        return serverSocket;
    }

    public Socket waitForClient() throws IOException {
        Socket clientSocket = createClientSocket();
        callback.onStatusUpdate(UPDATE_WAITING_FOR_CLIENT);
        return clientSocket;
    }

    private Socket createClientSocket() throws IOException {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client Connected " + clientSocket.getInetAddress().getHostAddress());
        callback.onStatusUpdate(UPDATE_CLIENT_SOCKET_CONNECTED);
        return clientSocket;
    }

    public void closeServerSocket() throws IOException {
        if (serverSocket != null) {
            serverSocket.close();
            System.out.println("Server socket closed");
        } else {
            serverSocket.close();
        }
    }

}
