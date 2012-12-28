package com.rvc;

import com.rvc.gui.ServerGui;
import com.rvc.server.Server;
import com.rvc.server.ServerSettings;

import java.io.IOException;
import java.net.UnknownHostException;

public class Main {

    private Server server;
    private ServerSettings serverSettings;
    private ServerGui serverGui;
    private Discovery discovery;

    public static void main(String args[]) {
        Main instance = new Main();

        instance.initServerSettings();
        instance.initGui();
        instance.startDiscovery();
        try {
            instance.serverLoop();
        } catch (IOException e) {
            // Server was closed whilst waiting for a connection
        }
        instance.finish();
    }

    private void initServerSettings() {
        try {
            serverSettings = new ServerSettings();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            // this should never happen
        }
    }

    private void initGui() {
        serverGui = new ServerGui(serverSettings);
    }

    private void startDiscovery() {
        try {
            discovery = new Discovery(serverSettings);
            discovery.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        discovery.unregister();
        serverGui.finish();
        System.exit(0);
    }

}
