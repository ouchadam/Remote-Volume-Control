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

    private final ConnectionState connectionState;

    private ServerCallbacks callback;
    private boolean ioOpened;
    private int port;
    private PrintWriter out;

    public Server(int port) {
        setPort(port);
        ioOpened = false;
        connectionState = new ConnectionState();
    }

    private void setPort(int port) {
        this.port = port;
    }

    private int getPort() {
        return this.port;
    }

    public void setCallback(ServerCallbacks callback) {
        this.callback = callback;
    }

    public boolean startServer() {
        System.out.println("Server start");
        setStatus("Server start");

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
        System.out.println("Waiting for client");
        setStatus("Waiting for client");
        try {
            clientSocket = serverSocket.accept();
            System.out.println("Client Socket Connected");
            setStatus("Client Socket Conn");
            connectionState.setSocketsOpened(true);
            connectionState.setServerRunning(true);
        } catch (IOException e) {
            System.out.println("Could not connect");
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
            setError("Error : IO failed");
            connectionState.setServerRunning(false);
        }
        return out;
    }

    private BufferedReader createInputIO(BufferedReader in, Socket clientSocket) {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ioOpened = true;
        } catch (IOException e) {
            e.printStackTrace();
            setError("Error : IO failed");
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
        if (ioOpened) {
            try {
                out.flush();
                out.close();
                in.close();
                this.ioOpened = false;
                System.out.println("IO closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeSockets(Socket clientSocket, ServerSocket serverSocket) {
        if (connectionState.getSocketsOpened()) {
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

    private void setError(String error) {
        callback.onErrorUpdate(error);
    }

    public void quit() {
        connectionState.setServerRunning(false);
        connectionState.setServerQuit(true);
    }

    public boolean getQuit() {
        return connectionState.getServerQuit();
    }

    private void setStatus(String status) {
        callback.onStatusUpdate(status);
    }

    @Override
    public void onCountdown(int value) {
        System.out.print('\r' + "TIMEOUT : " + value);
        setStatus("Timeout : " + value);
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
        setStatus("Client Connected");
    }
}
