package com.adam.rvc.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.adam.rvc.R;

public class SettingsActivity extends RVCActivity implements CompoundButton.OnCheckedChangeListener {

    private CheckBox checkbox;
    private SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        myPrefs = getSharedPreferences("prefs", MODE_PRIVATE);
        initCheckbox();
    }

    private void initCheckbox() {
        checkbox = (CheckBox) findViewById(R.id.settings_checkbox_show_server_data);
        checkbox.setChecked(myPrefs.getBoolean("server", false));
        checkbox.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean("server", b);
        prefsEditor.commit();
    }
}
