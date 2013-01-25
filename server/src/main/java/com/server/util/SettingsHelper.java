package com.server.util;

import com.server.server.ServerSettings;

import java.io.IOException;
import java.net.InetAddress;

public class SettingsHelper {

    public static ServerSettings getServerSettings(String interfaceName) {
        NetworkHelper networkHelper = new NetworkHelper(interfaceName);
        try {
            InetAddress localIp = networkHelper.getLocalIp();
            String macAddress = networkHelper.getMacAddress(localIp.getHostName());
            return new ServerSettings(macAddress, networkHelper.getExternalIp(), localIp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
