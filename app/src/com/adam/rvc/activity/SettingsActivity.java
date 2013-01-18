package com.adam.rvc.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.adam.rvc.R;

public class SettingsActivity extends RVCActivity implements CompoundButton.OnCheckedChangeListener {

    private SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        myPrefs = getSharedPreferences("prefs", MODE_PRIVATE);
        initCheckboxs();
    }

    private void initCheckboxs() {
        initCheckBox(R.id.settings_checkbox_show_server_data, "server");
        initCheckBox(R.id.settings_checkbox_show_message_data, "show_message");
    }

    private void initCheckBox(int resId, String prefsLocation) {
        CheckBox checkbox = (CheckBox) findViewById(resId);
        checkbox.setChecked(myPrefs.getBoolean(prefsLocation, false));
        checkbox.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        switch (compoundButton.getId()) {
            case R.id.settings_checkbox_show_server_data :
                prefsEditor.putBoolean("server", b);
                break;
            case R.id.settings_checkbox_show_message_data :
                prefsEditor.putBoolean("show_message", b);
                break;

        }
        prefsEditor.commit();
    }
}
