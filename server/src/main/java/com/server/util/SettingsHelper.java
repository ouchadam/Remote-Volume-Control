package com.server.util;

import com.server.server.ServerSettings;

import java.net.InetAddress;

public class SettingsHelper {

    private final NetworkHelper networkHelper;

    public SettingsHelper(NetworkHelper networkHelper) {
        this.networkHelper = networkHelper;
    }

    public ServerSettings getServerSettings() {
        InetAddress localIp = networkHelper.getLocalIp();
        String macAddress = networkHelper.getMacAddress(localIp.getHostName());
        return new ServerSettings(macAddress, localIp);
    }

}
