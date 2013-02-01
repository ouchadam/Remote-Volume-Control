package com.adam.rvc.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.adam.rvc.R;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }

}
