package com.rvc.gui;

import com.rvc.gui.tray.TrayExitCallback;
import com.rvc.server.Server;
import com.rvc.server.ServerCallbacks;
import com.rvc.server.ServerSettings;

import javax.swing.*;

public class ServerGui extends JFrame implements ServerCallbacks, TrayExitCallback {

    private final GuiCreator guiCreator;

    private Server server;

    public ServerGui(ServerSettings serverSettings) {
        guiCreator = new GuiCreator(new LabelManager(serverSettings), this);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                guiCreator.create();
            }
        });
    }

    public void attachServer(Server server) {
        this.server = server;
        server.setCallback(this);
    }

    private void serverExit() {
        System.out.println("Server Quitting");
        server.quit();
    }

    @Override
    public void onStatusUpdate(String update) {
        guiCreator.updateStatus(update);
    }

    @Override
    public void onErrorUpdate(String update) {
        guiCreator.updateError(update);
    }

    @Override
    public void onClientConnected() {
        guiCreator.showClientConnectedPopup();
    }

    @Override
    public void onClientDisconnected() {
        guiCreator.showClientDisconnectedPopup();
    }

    public void finish() {
        server = null;
        dispose();
    }

    @Override
    public void onTrayExit() {
        serverExit();
    }

}
