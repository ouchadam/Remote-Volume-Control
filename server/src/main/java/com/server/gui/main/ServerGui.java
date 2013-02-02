package com.server.gui.main;

import com.server.discovery.Discovery;
import com.server.ServerController;
import com.server.gui.tray.TrayExitCallback;
import com.server.server.ClientConnectionCallback;
import com.server.server.ServerSettings;

import javax.swing.*;
import java.io.IOException;

public class ServerGui implements ClientConnectionCallback, TrayExitCallback, Discovery.Callback {

    private final GuiCreator guiCreator;
    private final ServerController serverController;

    public ServerGui(ServerController serverController) {
        guiCreator = new GuiCreator(this, serverController);
        this.serverController = serverController;
        createGui();
    }

    private void createGui() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                guiCreator.create();
            }
        });
    }

    private void serverExit() throws IOException {
        System.out.println("Server Quitting");
        serverController.stopServer();
    }

    @Override
    public void onStatusUpdate(String update) {
        guiCreator.updateStatus(update);
    }

    @Override
    public void onDiscoveryStatusUpdate(String update) {
        guiCreator.updateDiscoveryStatus(update);
    }

    @Override
    public void onClientConnected(String client) {
        guiCreator.showClientConnectedPopup();
    }

    @Override
    public void onClientDisconnected(String client) {
        guiCreator.showClientDisconnectedPopup();
    }

    @Override
    public void onTrayExit() throws IOException {
        serverExit();
    }

    @Override
    public void onDiscoveryUpdate(String update) {
        onDiscoveryStatusUpdate(update);
    }

    public ServerSettings getServerSettings() {
        return guiCreator.getServerSettings();
    }

}
