package com.server;

import com.server.gui.main.ServerGui;
import com.server.server.Server;

import java.io.IOException;

public class Main implements ServerController {

    private static Main instance;

    private Server server;
    private ServerGui serverGui;

    public static void main(String args[]) {
        instance = new Main();
        instance.initGui();
    }

    private void initGui() {
        serverGui = new ServerGui(Main.this);
    }

    @Override
    public void startServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    initServer();
                    server.startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initServer() throws IOException {
        server = new Server(serverGui.getServerSettings());
        server.setConnectionCallback(serverGui);
        server.setMessageCallback(new ServerMessageHandler());
    }

    @Override
    public void stopServer() throws IOException {
        instance.finish();
    }

    private void finish() throws IOException {
        server.quit();
        System.exit(0);
    }

}
