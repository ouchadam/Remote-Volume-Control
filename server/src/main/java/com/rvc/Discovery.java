package com.rvc;

import com.rvc.server.ServerSettings;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;

public class Discovery {

    private static final String SERVICE_TYPE = "my-service-type";

    private final JmDNS mdnsServer;
    private final ServerSettings settings;

    public Discovery(ServerSettings serverSettings) throws IOException {
        mdnsServer = JmDNS.create(serverSettings.getInternalInet());
        settings = serverSettings;
    }

    public void start() throws IOException {
        ServiceInfo testService = createService();
        registerService(testService);
    }

    ServiceInfo createService() {
        return ServiceInfo.create(SERVICE_TYPE, "Test Service", settings.getPort(), "test service");
    }

    private void registerService(ServiceInfo testService) throws IOException {
        System.out.println("registering service");
        mdnsServer.registerService(testService);
        System.out.println("registered");
    }

    public void unregister() {
        mdnsServer.unregisterAllServices();
    }

}
