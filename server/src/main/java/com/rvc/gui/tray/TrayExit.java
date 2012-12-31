package com.rvc.gui.tray;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TrayExit implements ActionListener {

    private final TrayExitCallback trayExitCallback;
    private final MenuItem exitItem;



    public TrayExit(TrayExitCallback trayExitCallback) {
        this.trayExitCallback = trayExitCallback;
        exitItem = new MenuItem("Exit");
        exitItem.addActionListener(this);
    }

    public MenuItem getExitItem() {
        return exitItem;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MenuItem item = (MenuItem) e.getSource();
        System.out.println(item.getLabel());
        if ("Exit".equals(item.getLabel())) {
            trayExitCallback.onTrayExit();
        }
    }
}
