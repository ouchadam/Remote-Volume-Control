package com.adam.rvc.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.adam.rvc.util.MessageHandler;

public class ServerMessageReceiver extends BaseReceiver {

    private OnMessageReceived messageListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MessageHandler.ACTION_SERVER_MESSAGE)) {
            messageListener.onMessageReceived(intent.getStringExtra(MessageHandler.EXTRA_MESSAGE));
        }
    }

    @Override
    public IntentFilter getIntentFilter() {
        return new IntentFilter(MessageHandler.ACTION_SERVER_MESSAGE);
    }

    public void setMessageHandler(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }

}
