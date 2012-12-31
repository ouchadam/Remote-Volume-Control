package com.rvc.gui;

import com.rvc.gui.tray.TrayExit;
import com.rvc.gui.tray.TrayExitCallback;
import com.sun.javaws.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public class GuiCreator {

    private static final String FRAME_LABEL = "RVC Server";

    private final LabelManager labelManager;
    private final JFrame frame;
    private final TrayExitCallback trayExitCallback;
    private TrayIcon trayIcon;

    public GuiCreator(LabelManager labelManager, TrayExitCallback trayExitCallback) {
        this.labelManager = labelManager;
        this.trayExitCallback = trayExitCallback;
        frame = new JFrame(FRAME_LABEL);
    }

    public void create() {
        initFrame();
        initTray();
        initGuiExitAction();
    }

    private void initFrame() {
        frame.setBounds(100, 100, 250, 150);
        frame.add(createPanel());
        frame.setVisible(true);
    }

    private void initTray() {
        TrayExit trayExit = new TrayExit(trayExitCallback);
        PopupMenu popup = new PopupMenu();
        popup.add(trayExit.getExitItem());
        initTrayIcon(popup);
        addIconToTray();
        addTrayOnDoubleClickAction();
    }

    private void initTrayIcon(PopupMenu popup) {
        trayIcon = new TrayIcon(createImage("tray.png", "tray icon"));
        trayIcon.setPopupMenu(popup);
    }

    private void addIconToTray() {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
    }

    private void addTrayOnDoubleClickAction() {
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

    private void initGuiExitAction() {
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frame.setVisible(false);
            }
        });
    }

    public void showClientConnectedPopup() {
        displayPopUp("Client Connected");
    }

    public void showClientDisconnectedPopup() {
        displayPopUp("Client Disconnected");
    }

    private void displayPopUp(String message) {
        trayIcon.displayMessage(message, "", TrayIcon.MessageType.INFO);
    }

    public void updateStatus(String update) {
        labelManager.setStatusText(update);
    }

    public void updateError(String update) {
        labelManager.setErrorText(update);
    }

}
