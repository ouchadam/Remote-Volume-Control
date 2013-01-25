package com.server.gui.main;

import com.server.server.ServerSettings;

import javax.swing.*;

public class LabelManager {

    private final JLabel osName;
    private final JLabel internalIp;
    private final JLabel externalIp;
    private final JLabel macAddress;
    private final JLabel port;
    private final JLabel status;
    private final JLabel discoveryStatus;

    private final ServerSettings serverSettings;

    public LabelManager(ServerSettings serverSettings) {
        this.serverSettings = serverSettings;
        osName = new JLabel(getOsNameLabel());
        internalIp = new JLabel();
        externalIp = new JLabel();
        macAddress = new JLabel();
        port = new JLabel();
        status = new JLabel("Initialising server...");
        discoveryStatus = new JLabel("Initialising discovery");
    }

    private String getOsNameLabel() {
        return "OS : " + System.getProperty("os.name");
    }

    public void addAllLabels(JPanel panel) {
        panel.add(osName);
        panel.add(internalIp);
        panel.add(externalIp);
        panel.add(macAddress);
        panel.add(port);
        panel.add(status);
        panel.add(discoveryStatus);
    }

    public void setStatusText(final String update) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                status.setText(update);
            }
        });
    }

    public void discoveryStatus(final String update) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                discoveryStatus.setText(update);
            }
        });
    }

    public void update() {
        internalIp.setText(getInternalLabel());
        externalIp.setText(getExternalLabel());
        macAddress.setText(getMacAddressLabel());
        port.setText(getPortLabel());
    }

    private String getInternalLabel() {
        return "Internal Address : " + serverSettings.getInternalIp();
    }

    private String getExternalLabel() {
        return "External Address : " + serverSettings.getExternalIp();
    }

    private String getMacAddressLabel() {
        return "Mac Address : " + serverSettings.getMacAddress();
    }

    private String getPortLabel() {
        return "Port : " + serverSettings.getPort();
    }

}
