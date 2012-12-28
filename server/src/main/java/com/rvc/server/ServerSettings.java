package com.rvc.server;

import com.rvc.util.IPHelper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

public class ServerSettings {

    private final String macAddress;
    private final String externalIp;
    private final InetAddress internalAddress;

    public ServerSettings() throws UnknownHostException {
        internalAddress = IPHelper.getLocalIp();
        macAddress = parseMacAddress();
        externalIp = retrieveExternalIp();
    }

    private String retrieveExternalIp() {
        try {
            return IPHelper.getExternalIp();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error fetching";
        }
    }

    public String parseMacAddress() {
        String out = "";
        try {
            InetAddress address = InetAddress.getByName(getInternalIp());
            NetworkInterface network = NetworkInterface.getByInetAddress(address);
            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            out = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    public String getInternalIp() {
        return internalAddress.getHostAddress();
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getExternalIp() {
        return externalIp;
    }

    public int getPort() {
        return 5555;
    }

    public InetAddress getInternalInet() {
        return internalAddress;
    }
}
