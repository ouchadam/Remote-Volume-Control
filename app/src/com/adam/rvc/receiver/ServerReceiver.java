package com.adam.rvc.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import com.adam.rvc.server.ServerData;
import com.adam.rvc.util.ServerDiscoverer;

public class ServerReceiver extends BaseReceiver {

    private final OnServerReceived onServerReceivedListener;

    public ServerReceiver(OnServerReceived onServerReceived) {
        this.onServerReceivedListener = onServerReceived;
    }

    @Override
    public IntentFilter getIntentFilter() {
        return new IntentFilter(ServerDiscoverer.ACTION_SERVER_DATA_RECEIVED);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        onServerReceivedListener.onIpReceived(getServerData(intent.getParcelableExtra(ServerDiscoverer.EXTRA_SERVER_DATA)));
    }

    private ServerData getServerData(Parcelable data) {
        return (ServerData) data;
    }

}
