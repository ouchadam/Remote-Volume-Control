package com.server.discovery;

import com.server.server.ServerSettings;

import java.net.InetAddress;

public class DiscoverySettings {

    private final ServerSettings serverSettings;
    private final String serverType;

    public DiscoverySettings(ServerSettings serverSettings, String serverType) {
        this.serverSettings = serverSettings;
        this.serverType = serverType;
    }

    public int getPort() {
        return serverSettings.getPort();
    }

    public InetAddress getInternalAddress() {
        return serverSettings.getInternalInet();
    }

    public String getServiceType() {
        return serverType;
    }

}
