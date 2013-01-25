package com.server;

import com.server.server.ServerSettings;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DiscoveryShould {

    @Test
    public void use_provided_port_for_server() throws IOException {
        ServerSettings settings = mock(ServerSettings.class);
        Discovery discovery = new Discovery(settings, null);
        discovery.createService();

        verify(settings).getPort();
    }

}
