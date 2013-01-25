package com.server.server;


import java.net.InetAddress;

public class ServerSettings {

    private final String macAddress;
    private final String externalIp;
    private final InetAddress internalAddress;

    public ServerSettings(String macAddress, String externalIp, InetAddress internalAddress) {
        this.macAddress = macAddress;
        this.externalIp = externalIp;
        this.internalAddress = internalAddress;
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
