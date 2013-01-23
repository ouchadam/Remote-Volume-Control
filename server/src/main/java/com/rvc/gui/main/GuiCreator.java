package com.rvc.gui.main;

import com.rvc.Main;
import com.rvc.ServerController;
import com.rvc.gui.tray.TrayExit;
import com.rvc.gui.tray.TrayExitCallback;
import com.rvc.util.IPHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public class GuiCreator extends JFrame {

    private static final String FRAME_LABEL = "RVC Server";

    private final LabelManager labelManager;
    private final TrayExitCallback trayExitCallback;
    private final ServerController serverController;

    private TrayIcon trayIcon;
    private JPanel panel;
    private JComboBox comboBox;

    public GuiCreator(LabelManager labelManager, TrayExitCallback trayExitCallback, ServerController serverController) {
        this.labelManager = labelManager;
        this.trayExitCallback = trayExitCallback;
        this.serverController = serverController;
    }

    public void create() {
        initFrame();
        initTray();
        initGuiExitAction();
    }

    private void initFrame() {
        setTitle(FRAME_LABEL);
        setBounds(100, 100, 250, 150);
        add(createInitPanel());
        setVisible(true);
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
                setVisible(true);
            }
        });
    }

    private JPanel createInitPanel() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(createComboBox());
        panel.add(createStartServerButton());
        return panel;
    }

    private Button createStartServerButton() {
        Button button = new Button();
        button.setLabel("Start Server!");
        button.addActionListener(buttonAction);
        return button;
    }

    ActionListener buttonAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            startServer();
        }
    };

    private void startServer() {
        showServerSettings();
        serverController.startServer();
    }

    private void showServerSettings() {
        remove(panel);
        add(createMainPanel());
        invalidate();
        validate();
    }

    private JPanel createMainPanel() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        labelManager.update((String)comboBox.getItemAt(comboBox.getSelectedIndex()));
        labelManager.addAllLabels(panel);
        return panel;
    }

    private JComboBox createComboBox() {
        comboBox = new JComboBox();
        for (String name : IPHelper.getAdapters()) {
            comboBox.addItem(name);
        }
        return comboBox;
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
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
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

    public void updateDiscoveryStatus(String update) {
        labelManager.discoveryStatus(update);
    }

}
