package com.adam.rvc.service;

import android.content.Intent;
import com.adam.rvc.receiver.OnMessageReceived;
import com.adam.rvc.util.StatusUpdater;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class RVCBackgroundServiceShould {

    private final RVCClient rvcClient = mock(RVCClient.class);
    private final OnMessageReceived onMessageReceived = mock(OnMessageReceived.class);
    private final StatusUpdater statusUpdater = mock(StatusUpdater.class);

    private RVCConnection rvcConnection;
    private RVCBackgroundService rvcBackgroundServer;

    @Before
    public void setUp() throws IOException {
        rvcConnection = new RVCConnection(onMessageReceived, statusUpdater, rvcClient);
        rvcBackgroundServer = new RVCBackgroundService(rvcConnection);
    }

    @Test
    public void attempt_to_connect_on_action_start_intent() {
        rvcBackgroundServer.onStartCommand(new Intent(RVCBackgroundService.ACTION_START), 0 , 0);

        verify(rvcClient).connect();
    }

    @Test
    public void attempt_to_disconnect_in_service_ondestroy() {
        rvcBackgroundServer.onDestroy();

        verify(rvcClient).disconnect();
    }

    @Test
    public void write_message_to_server_on_write_message_action__write_intent() {
        rvcBackgroundServer.onStartCommand(new Intent(RVCBackgroundService.ACTION_WRITE), 0, 0);

        verify(rvcClient).writeToServer(anyString());
    }

}
