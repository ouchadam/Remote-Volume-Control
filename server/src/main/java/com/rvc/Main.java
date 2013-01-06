package com.rvc;

import com.rvc.gui.main.ServerGui;
import com.rvc.server.Server;
import com.rvc.server.ServerSettings;

import java.io.IOException;

public class Main implements ServerController {

    private static Main instance;

    private final ServerController serverController = this;

    private Server server;
    private ServerSettings serverSettings;
    private ServerGui serverGui;
    private Discovery discovery;

    public static void main(String args[]) {
        instance = new Main();
        instance.initServerSettings();
        instance.initGui();
    }

    private void initServerSettings() {
        serverSettings = new ServerSettings();
    }

    private void initGui() {
        serverGui = new ServerGui(serverSettings, serverController);
    }

    private void initServer() {
        server = new Server(serverSettings);
    }

    @Override
    public void startServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    instance.serverLoop();
                } catch (IOException e) {
                    // Server was closed whilst waiting for a connection
                }
            }
        }).start();
    }

    private void serverLoop() throws IOException {
        instance.startDiscovery();
        do {
            initServer();
            serverGui.attachServer(server);
        } while (server.startServer());
    }

    private void startDiscovery() {
        try {
            discovery = new Discovery(serverSettings);
            discovery.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopServer() {
        instance.finish();
    }

    private void finish() {
        if (discovery != null && serverGui != null) {
            discovery.unregister();
            serverGui.finish();
        }
        System.exit(0);
    }

}
