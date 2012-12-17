package com.rvc.gui;

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

public class ServerGui extends JFrame implements ServerCallbacks {

    private final JLabel status = new JLabel();
    private final JLabel error = new JLabel("Error : nothing so far...");
    private final ServerSettings serverSettings;

    private JFrame frame;
    private TrayIcon trayIcon;
    private JLabel osName, internalIp, externalIp, macAddress, port;
    private Server server;

    public ServerGui(ServerSettings serverSettings) {
        this.serverSettings = serverSettings;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                updateGui();
                onServerExit();
            }
        });
    }

    public void setServer(Server server) {
        this.server = server;
        server.setCallback(this);
    }

    private void createAndShowGUI() {
        final PopupMenu popup = new PopupMenu();
        trayIcon = new TrayIcon(createImage("tray.png", "tray icon"));
        SystemTray tray = SystemTray.getSystemTray();

        MenuItem exitItem = new MenuItem("Exit");

        final JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        frame = new JFrame("RVC Server");

        osName = new JLabel("");
        internalIp = new JLabel("");
        externalIp = new JLabel("");
        macAddress = new JLabel("");
        port = new JLabel("");

        frame.setBounds(100, 100, 250, 150);
        panel.add(osName);
        panel.add(internalIp);
        panel.add(externalIp);
        panel.add(macAddress);
        panel.add(port);
        panel.add(status);
        panel.add(error);

        frame.add(panel);
        frame.setVisible(true);

        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }

        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(true);
            }
        });

        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MenuItem item = (MenuItem) e.getSource();
                System.out.println(item.getLabel());
                if ("Update".equals(item.getLabel())) {
                    updateGui();
                } else if ("Exit".equals(item.getLabel())) {
                    serverExit();
                }
            }
        };

        exitItem.addActionListener(listener);
    }

    private void serverExit() {
        System.out.println("Server Quitting");
        server.quit();
        System.exit(0);
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

    private void updateGui() {
        osName.setText("OS : " + System.getProperty("os.name"));
        internalIp.setText("Lan Address : " + serverSettings.getInteralIp());
        externalIp.setText("External Address : " + serverSettings.getExternalIp());
        macAddress.setText("Mac Address : " + serverSettings.getMacAddress());
        port.setText("Port : " + serverSettings.getPort());
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
        status.setText(update);
    }

    @Override
    public void onErrorUpdate(String update) {
        error.setText(update);
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
}
