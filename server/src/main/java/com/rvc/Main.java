package com.rvc;

import com.rvc.gui.ServerGui;
import com.rvc.server.Server;
import com.rvc.server.ServerSettings;

public class Main {

    private Server server;
    private ServerSettings serverSettings;
    private ServerGui serverGui;

    public static void main(String args[]) {
        Main instance = new Main();

        instance.initServer();
        instance.initGui();
        instance.serverLoop();
    }

    private void initServer() {
        serverSettings = new ServerSettings();
        server = new Server(serverSettings);
    }

    private void initGui() {
        serverGui = new ServerGui(serverSettings);
        serverGui.setServer(server);
    }

    private void serverLoop() {
        while (server.startServer()) {
            initServer();
            serverGui.setServer(server);
        }
    }

}
