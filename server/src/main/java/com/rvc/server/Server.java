package com.rvc.server;

import com.rvc.util.ConnectionTimeout;
import com.rvc.util.Countdown;

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
    private static final String ERROR_COULD_NOT_CONNECT = "Could not connect";
    private static final String ERROR_IO_FAILED = "Error : IO failed";

    private final ConnectionState connectionState;

    private ServerCallbacks callback;
    private int port;
    private PrintWriter out;

    public Server(ServerSettings serverSettings) {
        port = serverSettings.getPort();
        connectionState = new ConnectionState();
    }

    private int getPort() {
        return this.port;
    }

    public void setCallback(ServerCallbacks callback) {
        this.callback = callback;
    }

    public boolean startServer() {
        System.out.println(UPDATE_SERVER_START);
        updateStatus(UPDATE_SERVER_START);

        Socket clientSocket = null;
        ServerSocket serverSocket = null;

        serverSocket = createServerSocket(serverSocket, getPort());
        clientSocket = createClientSocket(clientSocket, serverSocket);

        BufferedReader in = null;

        out = createOutputIO(clientSocket);
        in = createInputIO(in, clientSocket);

        SocketReader readSocket = new SocketReader(in, connectionState, this);
        new Countdown(this, connectionState);
        readSocket.join();

        callback.onClientDisconnected();

        closeSockets(clientSocket, serverSocket);
        closeIO(in, out);

        if (getQuit()) {
            System.exit(1);
            return false;
        }
        return true;
    }

    private ServerSocket createServerSocket(ServerSocket serverSocket, int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("couldn't connect : IOException || is the server already running?");
            System.exit(1);
        }
        return serverSocket;
    }

    private Socket createClientSocket(Socket clientSocket, ServerSocket serverSocket) {
        System.out.println(UPDATE_WAITING_FOR_CLIENT);
        updateStatus(UPDATE_WAITING_FOR_CLIENT);
        try {
            clientSocket = serverSocket.accept();
            System.out.println(UPDATE_CLIENT_SOCKET_CONNECTED);
            updateStatus(UPDATE_CLIENT_SOCKET_CONNECTED);
            connectionState.setSocketsOpened(true);
            connectionState.setServerRunning(true);
        } catch (IOException e) {
            System.out.println(ERROR_COULD_NOT_CONNECT);
            updateError(ERROR_COULD_NOT_CONNECT);
            e.printStackTrace();
        }
        return clientSocket;
    }

    private PrintWriter createOutputIO(Socket clientSocket) {
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

    private BufferedReader createInputIO(BufferedReader in, Socket clientSocket) {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            connectionState.setIoOpened(true);
        } catch (IOException e) {
            e.printStackTrace();
            updateError(ERROR_IO_FAILED);
            connectionState.setServerRunning(false);
        }
        return in;
    }

    @Override
    public void onReceiveMessage(String message) {
        if (message.substring(1, 2).equals("1")) {
            out.println(message);
        }
    }

    public void closeIO(BufferedReader in, PrintWriter out) {
        if (connectionState.isIoOpened()) {
            try {
                out.flush();
                out.close();
                in.close();
                connectionState.setIoOpened(false);
                System.out.println("IO closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeSockets(Socket clientSocket, ServerSocket serverSocket) {
        if (connectionState.isSocketsOpened()) {
            try {
                serverSocket.close();
                clientSocket.close();
                System.out.println("Sockets closed");
                connectionState.setSocketsOpened(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateError(String error) {
        callback.onErrorUpdate(error);
    }

    public void quit() {
        connectionState.setServerRunning(false);
        connectionState.setServerQuit(true);
    }

    public boolean getQuit() {
        return connectionState.getServerQuit();
    }

    private void updateStatus(String status) {
        callback.onStatusUpdate(status);
    }

    @Override
    public void onCountdown(int value) {
        System.out.print('\r' + "TIMEOUT : " + value);
        updateStatus("Timeout : " + value);
    }

    @Override
    public void onConnectionTimedOut() {
        System.out.println("\nClient Timed Out");
        connectionState.setServerRunning(false);
    }

    @Override
    public void onConnected() {
        System.out.println("\nClient Connection Confirmed");
        callback.onClientConnected();
        updateStatus("Client Connected");
    }
}
