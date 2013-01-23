package com.rvc.server;

import java.io.IOException;
import java.net.Socket;

public class ConnectionHandler {

    private final ConnectionCallbacks connectionCallbacks;
    private final ServerState serverState;
    private IOController ioController;

    public ConnectionHandler(ConnectionCallbacks connectionCallbacks, ServerState serverState) {
        this.connectionCallbacks = connectionCallbacks;
        this.serverState = serverState;
    }

    public void handleNewConnection(Socket clientSocket) throws IOException {
        ioController = initIO(clientSocket);
        ioController.openIO();
        SocketReader socketReader = createSocketReadingThread(ioController, socketReaderCallback);
        connectionCallbacks.clientThreadEstablished(this);
        socketReader.start();
    }

    private IOController initIO(Socket clientSocket) throws IOException {
        return new IOHandler(clientSocket);
    }


    private SocketReader.ReceiverCallback socketReaderCallback = new SocketReader.ReceiverCallback() {
        @Override
        public void onReceiveMessage(String message) {
            if (isValidMessage(message)) {
                ioController.printWriter().println(message);
            }
        }

        @Override
        public void onConnectionFinished() throws IOException {
            connectionFinished();
        }

    };

    private boolean isValidMessage(String message) {
        return message.substring(1, 2).equals("1");
    }


    private SocketReader createSocketReadingThread(IOController ioController, SocketReader.ReceiverCallback receiverCallback) {
        return new SocketReader(ioController.bufferReader(), serverState, receiverCallback);
    }

    private void connectionFinished() throws IOException {
        sendDisconnectPacket();
        closeIO();
        connectionCallbacks.clientThreadFinished(this);
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

    public void stop() {
        sendDisconnectPacket();
        closeIO();
    }

    interface ConnectionCallbacks {

        void clientThreadEstablished(ConnectionHandler connectionHandler);

        void clientThreadFinished(ConnectionHandler connectionHandler);

    }

}
