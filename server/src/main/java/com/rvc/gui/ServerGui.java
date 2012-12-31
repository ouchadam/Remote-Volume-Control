package com.rvc.gui;

import com.rvc.gui.tray.TrayExit;
import com.rvc.gui.tray.TrayExitCallback;
import com.rvc.server.Server;
import com.rvc.server.ServerCallbacks;
import com.rvc.server.ServerSettings;
import com.sun.javaws.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public class ServerGui extends JFrame implements ServerCallbacks, TrayExitCallback {

    private static final String FRAME_LABEL = "RVC Server";

    private final LabelManager labelManager;

    private JFrame frame;
    private TrayIcon trayIcon;
    private Server server;

    public ServerGui(ServerSettings serverSettings) {
        labelManager = new LabelManager(serverSettings);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                onServerExit();
            }
        });
    }

    public void attachServer(Server server) {
        this.server = server;
        server.setCallback(this);
    }

    private void createAndShowGUI() {
        initFrame();
        initTray();
    }

    private void initFrame() {
        frame = new JFrame(FRAME_LABEL);
        frame.setBounds(100, 100, 250, 150);
        frame.add(createPanel());
        frame.setVisible(true);
    }

    private void initTray() {
        TrayExit trayExit = new TrayExit(this);
        PopupMenu popup = new PopupMenu();
        popup.add(trayExit.getExitItem());

        trayIcon = new TrayIcon(createImage("tray.png", "tray icon"));
        trayIcon.setPopupMenu(popup);

        try {
            SystemTray tray = SystemTray.getSystemTray();
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }

        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(true);
            }
        });
    }

    private JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        labelManager.addAllLabels(panel);
        return panel;
    }

    private void serverExit() {
        System.out.println("Server Quitting");
        server.quit();
    }

    private Image createImage(String fileName, String description) {
        URL imageURL = Main.class.getResource("/" + fileName);
        if (imageURL == null) {
            System.err.println("Resource not found: " + fileName);
            System.exit(1);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

    private void onServerExit() {
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frame.setVisible(false);
            }
        });
    }

    @Override
    public void onStatusUpdate(String update) {
        labelManager.setStatusText(update);
    }

    @Override
    public void onErrorUpdate(String update) {
        labelManager.setErrorText(update);
    }

    @Override
    public void onClientConnected() {
        showPopup("Client Connected");
    }

    @Override
    public void onClientDisconnected() {
        showPopup("Client Disconnected");
    }

    private void showPopup(String message) {
        trayIcon.displayMessage(message, "", TrayIcon.MessageType.INFO);
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
