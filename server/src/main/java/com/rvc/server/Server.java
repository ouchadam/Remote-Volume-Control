package com.rvc.server;

import com.rvc.ServerController;
import com.rvc.util.ConnectionTimeout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements SocketReader.ReceiverCallback, ConnectionTimeout {

    private static final String UPDATE_SERVER_START = "Server start";
    private static final String UPDATE_WAITING_FOR_CLIENT = "Waiting for client";
    private static final String UPDATE_CLIENT_SOCKET_CONNECTED = "Client Socket Conn";
    private static final String ERROR_IO_FAILED = "Error : IO failed";
    private static final String ERROR_SERVER_SOCKET_IO_EXCEPTION = "couldn't connect : IOException || is the server already running?";

    private final ConnectionState connectionState;

    private ServerCallbacks callback;
    private int port;
    private PrintWriter out;
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private BufferedReader in;

    public Server(ServerSettings serverSettings) {
        port = serverSettings.getPort();
        connectionState = new ConnectionState();
    }

    public void setCallback(ServerCallbacks callback) {
        this.callback = callback;
    }

    public boolean startServer() throws IOException {
        updateStatus(UPDATE_SERVER_START);
        initConnection();
        new SocketReader(in, connectionState, this).start().join();
        callback.onClientDisconnected();
        closeConnection();
        return isServerToBeRestarted();
    }

    private void initConnection() throws IOException {
        createSockets();
        createIO();
    }

    private void createSockets() throws IOException {
        serverSocket = createServerSocket();
        clientSocket = createClientSocket();
    }

    private void createIO() throws IOException {
        out = createOutput();
        in = createInput();
    }

    private ServerSocket createServerSocket() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println(ERROR_SERVER_SOCKET_IO_EXCEPTION);
            System.exit(1);
        }
        return serverSocket;
    }

    private Socket createClientSocket() throws IOException {
        updateStatus(UPDATE_WAITING_FOR_CLIENT);
        clientSocket = serverSocket.accept();
        updateStatus(UPDATE_CLIENT_SOCKET_CONNECTED);
        connectionState.setServerRunning(true);
        return clientSocket;
    }

    private PrintWriter createOutput() {
        PrintWriter out = null;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            updateError(ERROR_IO_FAILED);
            connectionState.setServerRunning(false);
        }
        return out;
    }

    private BufferedReader createInput() throws IOException {
        return new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void onReceiveMessage(String message) {
        if (message.substring(1, 2).equals("1")) {
            sendMessage(message);
        }
    }

    private void sendMessage(String message) {
        System.out.println("sending : " + message);
        out.println(message);
    }

    private void closeConnection() {
        closeIO();
        closeSockets();
    }

    private void closeIO() {
        try {
            if (out != null && in != null) {
                out.flush();
                out.close();
                in.close();
                System.out.println("IO closed");
            } else {
                out = null;
                in = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeSockets() {
        try {
            if (serverSocket != null && clientSocket != null) {
                serverSocket.close();
                clientSocket.close();
                System.out.println("Sockets closed");
            } else {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateError(String error) {
        System.out.println(error);
        callback.onErrorUpdate(error);
    }

    public void quit() {
        if (out != null) {
            sendDisconnectPacket();
        }
        connectionState.setServerRunning(false);
        connectionState.setServerQuit(true);
        closeConnection();
    }

    private void sendDisconnectPacket() {
        sendMessage("1000");
    }

    private boolean isServerToBeRestarted() {
        return !connectionState.getServerQuit();
    }

    private void updateStatus(String status) {
        System.out.println(status);
        callback.onStatusUpdate(status);
    }

    @Override
    public void onCountdown(int value) {
        updateStatus("Timeout : " + value);
    }

    @Override
    public void onConnectionTimedOut() {
        updateStatus("Client Timed Out");
        connectionState.setServerRunning(false);
        connectionState.setClientConnected(false);
    }

    @Override
    public void onConnected() {
        updateStatus("Client Connected");
        callback.onClientConnected();
    }
}
