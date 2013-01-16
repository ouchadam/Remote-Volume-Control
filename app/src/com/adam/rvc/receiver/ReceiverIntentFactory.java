package com.adam.rvc.receiver;

import android.content.Intent;
import com.adam.rvc.server.ServerData;
import com.adam.rvc.util.MessageHandler;
import com.adam.rvc.discovery.ServerDiscoverer;
import com.adam.rvc.util.StatusUpdater;

public class ReceiverIntentFactory {

    public static Intent broadcastServerMessage(String message) {
        Intent intent = new Intent();
        intent.setAction(MessageHandler.ACTION_SERVER_MESSAGE);
        intent.putExtra(MessageHandler.EXTRA_MESSAGE, message);
        return intent;
    }

    public static Intent broadcastStatusMessage(String message) {
        Intent intent = new Intent();
        intent.setAction(StatusUpdater.ACTION_STATUS_MESSAGE);
        intent.putExtra(StatusUpdater.EXTRA_MESSAGE, message);
        return intent;
    }

    public static Intent broadcastServerData(ServerData serverData) {
        Intent intent = new Intent();
        intent.setAction(ServerDiscoverer.ACTION_SERVER_DATA_RECEIVED);
        intent.putExtra(ServerDiscoverer.EXTRA_SERVER_DATA, serverData);
        return intent;
    }
}
