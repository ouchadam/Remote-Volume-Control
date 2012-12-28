package com.rvc;

import com.rvc.server.ServerSettings;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;

public class Discovery {

    private static final String SERVICE_TYPE = "my-service-type";

    private final JmDNS mdnsServer;

    public Discovery(ServerSettings serverSettings) throws IOException {
        mdnsServer = JmDNS.create(serverSettings.getInternalInet());
    }

    public void start() throws IOException {
        ServiceInfo testService = createService();
        registerService(testService);
    }

    private void registerService(ServiceInfo testService) throws IOException {
        System.out.println("registering service");
        mdnsServer.registerService(testService);
        System.out.println("registered");
    }

    private ServiceInfo createService() {
        return ServiceInfo.create(SERVICE_TYPE, "Test Service", 5555, "test service");
    }

    public void unregister() {
        mdnsServer.unregisterAllServices();
    }
}
