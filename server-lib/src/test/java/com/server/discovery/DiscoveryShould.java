package com.server.discovery;

import com.server.discovery.Discovery;
import com.server.discovery.DiscoverySettings;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DiscoveryShould {

    @Test
    public void use_provided_port_for_server() throws IOException {
        DiscoverySettings settings = mock(DiscoverySettings.class);
        Discovery discovery = new Discovery(settings, null);
        discovery.createService();

        verify(settings).getPort();
    }

}
