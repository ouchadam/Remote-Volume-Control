package com.adam.rvc.util;

import android.content.Context;
import com.adam.rvc.service.RVCServiceFactory;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class MessageHandlerShould {

    private final SharedPrefsHelper sharedPrefsHelper = mock(SharedPrefsHelper.class);
    private final Context context = mock(Context.class);
    private final VolumeUpdater volumeUpdater = mock(VolumeUpdater.class);

    private MessageHandler messageHandler;

    @Before
    public void setUp() {
        when(sharedPrefsHelper.getShowMessageSetting()).thenReturn(false);
        messageHandler = new MessageHandler(context, sharedPrefsHelper, volumeUpdater);
    }

    @Test
    public void stop_rvc_background_service_on_exit_message_received() {
        messageHandler.onMessageReceived("1000");

        verify(context).stopService(RVCServiceFactory.stopService(context));
    }

    @Test
    public void update_volume_when_valid_volume_message_has_been_received() {
        messageHandler.onMessageReceived("002A");

        verify(volumeUpdater).updateVolume(anyInt());
    }

    @Test
    public void not_update_volume_when_invalid_volume_message_is_received() {
        messageHandler.onMessageReceived("00FF");

        verifyNoMoreInteractions(volumeUpdater);
    }

    @Test
    public void not_blow_up_or_wrongly_handle_invalid_message_content() {
        messageHandler.onMessageReceived("!@");

        verifyNoMoreInteractions(volumeUpdater);
        verifyNoMoreInteractions(context);
    }

    @Test
    public void not_blow_up_or_wrongly_handle_null_message_content() {
        messageHandler.onMessageReceived(null);

        verifyNoMoreInteractions(volumeUpdater);
        verifyNoMoreInteractions(context);
    }

    @Test
    public void not_blow_up_or_wrongly_handle_empty_message_content() {
        messageHandler.onMessageReceived("");

        verifyNoMoreInteractions(volumeUpdater);
        verifyNoMoreInteractions(context);
    }

}
