package com.adam.rvc.factory;

import android.content.Context;
import android.content.Intent;
import com.adam.rvc.activity.SettingsActivity;

public class ActivityIntentFactory {

    public static Intent getSettingsActivity(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

}
