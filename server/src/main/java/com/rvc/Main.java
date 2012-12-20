package com.rvc;

import com.rvc.gui.ServerGui;
import com.rvc.server.Server;
import com.rvc.server.ServerSettings;

import java.io.IOException;

public class Main {

    private Server server;
    private ServerSettings serverSettings;
    private ServerGui serverGui;

    public static void main(String args[]) {
        Main instance = new Main();

        instance.initServerSettings();
        instance.initGui();
        try {
            instance.serverLoop();
        } catch (IOException e) {
            // Server was closed whilst waiting for a connection
        }
        instance.finish();
    }

    private void initServerSettings() {
        serverSettings = new ServerSettings();
    }

    private void initGui() {
        serverGui = new ServerGui(serverSettings);
    }

    private void serverLoop() throws IOException {
        do {
            initServer();
            serverGui.setServer(server);
        } while(server.startServer());
    }

    private void initServer() {
        server = new Server(serverSettings);
    }

    private void finish() {
        serverGui.finish();
        System.exit(0);
    }

}
