package com.adam.rvc.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import com.adam.rvc.server.ServerData;

public class IPReceiver extends BaseReceiver {

    public static final String ACTION = "IP_ACTION";

    private final OnIpReceived ipReceivedListener;

    public IPReceiver(OnIpReceived ipReceivedListener) {
        this.ipReceivedListener = ipReceivedListener;
    }

    @Override
    public IntentFilter getIntentFilter() {
        return new IntentFilter(ACTION);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ipReceivedListener.onIpReceived(getServerData(intent.getParcelableExtra("data")));
    }

    private ServerData getServerData(Parcelable data) {
        return (ServerData) data;
    }

}
