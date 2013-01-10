package com.rvc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class IOHandler implements IOController {

    private final Socket clientSocket;

    private PrintWriter out;
    private BufferedReader in;

    public IOHandler(SocketHandler socketHandler) {
        this.clientSocket = socketHandler.clientSocket();
    }

    @Override
    public void openIO() throws IOException {
        in = createInput();
        out = createOutput();
    }

    private PrintWriter createOutput() throws IOException {
        return new PrintWriter(clientSocket.getOutputStream(), true);
    }

    private BufferedReader createInput() throws IOException {
        return new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void closeIO() throws IOException {
        if (out != null && in != null) {
            out.flush();
            out.close();
            in.close();
            System.out.println("IO closed");
        } else {
            out = null;
            in = null;
        }
    }

    @Override
    public BufferedReader bufferReader() {
        return in;
    }

    @Override
    public PrintWriter printWriter() {
        return out;
    }
}
