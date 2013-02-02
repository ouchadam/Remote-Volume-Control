package com.server.server;


import java.net.InetAddress;

public class ServerSettings {

    private final String macAddress;
    private final InetAddress internalAddress;

    public ServerSettings(String macAddress, InetAddress internalAddress) {
        this.macAddress = macAddress;
        this.internalAddress = internalAddress;
    }

    public String getInternalIp() {
        return internalAddress.getHostAddress();
    }

    public String getMacAddress() {
        return macAddress;
    }

    public int getPort() {
        return 5555;
    }

    public InetAddress getInternalInet() {
        return internalAddress;
    }

}
