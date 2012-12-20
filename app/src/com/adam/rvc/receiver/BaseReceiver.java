package com.adam.rvc.receiver;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

abstract class BaseReceiver extends BroadcastReceiver {

    public abstract IntentFilter getIntentFilter();

}
