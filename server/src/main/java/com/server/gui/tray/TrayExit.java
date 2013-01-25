package com.server.gui.tray;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class TrayExit implements ActionListener {

    private static final String EXIT_LABEL = "Exit";

    private final TrayExitCallback trayExitCallback;
    private final MenuItem exitItem;

    public TrayExit(TrayExitCallback trayExitCallback) {
        this.trayExitCallback = trayExitCallback;
        exitItem = new MenuItem(EXIT_LABEL);
        exitItem.addActionListener(this);
    }

    public MenuItem getExitItem() {
        return exitItem;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MenuItem item = (MenuItem) e.getSource();
        System.out.println(item.getLabel());
        if (EXIT_LABEL.equals(item.getLabel())) {
            try {
                trayExitCallback.onTrayExit();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}
