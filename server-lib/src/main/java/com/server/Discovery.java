package com.server;

import com.server.server.ServerSettings;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Discovery {

    private final JmDNS mdnsServer;
    private final ServerSettings settings;
    private final Callback callback;

    public interface Callback {
        void onDiscoveryUpdate(String update);
    }

    public Discovery(ServerSettings serverSettings, Callback callback) throws IOException {
        this.callback = callback;
        mdnsServer = JmDNS.create(serverSettings.getInternalInet());
        settings = serverSettings;
    }

    public void start() throws IOException {
        ServiceInfo testService = createService();
        registerService(testService);
    }

    ServiceInfo createService() {
        return ServiceInfo.create(settings.getServiceType(), createServiceName(), settings.getPort(), "RVC");
    }

    private String createServiceName() {
        try {
            return "RVC " + InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't retrieve host name");
        }
    }

    private void registerService(ServiceInfo testService) throws IOException {
        mdnsServer.registerService(testService);
        callback.onDiscoveryUpdate("Discovery ready");
    }

    public void finish() {
        mdnsServer.unregisterAllServices();
    }

}
