package com.server.server;

import java.io.IOException;
import java.net.Socket;

public class ConnectionHandler {

    private final Callback callback;
    private final ServerState serverState;
    private IOController ioController;
    private String clientName;

    interface Callback {
        void clientThreadEstablished(ConnectionHandler connectionHandler);
        void clientThreadFinished(ConnectionHandler connectionHandler);
        void clientMessageReceived(String clientName, String message);
    }

    public ConnectionHandler(Callback callback, ServerState serverState) {
        this.callback = callback;
        this.serverState = serverState;
    }

    public void handleNewConnection(Socket clientSocket) throws IOException {
        setClientName(clientSocket);
        ioController = initIO(clientSocket);
        ioController.openIO();
        SocketReader socketReader = createSocketReadingThread(ioController, socketReaderCallback);
        callback.clientThreadEstablished(this);
        socketReader.start();
    }

    private void setClientName(Socket clientSocket) {
        clientName = clientSocket.getInetAddress().getHostName();
    }

    private IOController initIO(Socket clientSocket) throws IOException {
        return new IOHandler(clientSocket);
    }


    private SocketReader.Callback socketReaderCallback = new SocketReader.Callback() {
        @Override
        public void onReceiveMessage(String message) {
            callback.clientMessageReceived(getClientName(), message);
        }

        @Override
        public void onConnectionFinished() {
            try {
                connectionFinished();
            } catch (IOException e) {
            }
        }

    };

    private SocketReader createSocketReadingThread(IOController ioController, SocketReader.Callback receiverCallback) {
        return new SocketReader(ioController.bufferReader(), serverState, receiverCallback);
    }

    private void connectionFinished() throws IOException {
        sendDisconnectPacket();
        closeIO();
        callback.clientThreadFinished(this);
    }

    private void sendDisconnectPacket() {
        ioController.printWriter().println("1000");
    }

    private void closeIO() {
        try {
            ioController.closeIO();
        } catch (IOException e) {
        }
    }

    public void writeToClient(String message) {
        ioController.printWriter().println(message);
    }

    public void stop() throws IOException {
        connectionFinished();
    }

    public String getClientName() {
        return clientName;
    }

}
