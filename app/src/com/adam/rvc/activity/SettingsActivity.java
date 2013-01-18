package com.adam.rvc.activity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.adam.rvc.R;
import com.adam.rvc.util.SharedPrefsHelper;

public class SettingsActivity extends RVCActivity implements CompoundButton.OnCheckedChangeListener {

    private SharedPrefsHelper myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        myPrefs = new SharedPrefsHelper(this);
        initCheckboxs();
    }

    private void initCheckboxs() {
        initCheckBox(R.id.settings_checkbox_show_server_data, SharedPrefsHelper.PREFS_SHOW_SERVER_DETAILS);
        initCheckBox(R.id.settings_checkbox_show_message_data, SharedPrefsHelper.PREFS_SHOW_MESSAGE_DETAILS);
    }

    private void initCheckBox(int resId, String prefsLocation) {
        CheckBox checkbox = (CheckBox) findViewById(resId);
        checkbox.setChecked(myPrefs.getBoolean(prefsLocation));
        checkbox.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checkedState) {
        switch (compoundButton.getId()) {
            case R.id.settings_checkbox_show_server_data :
                myPrefs.storeServerDetailsSetting(checkedState);
                break;
            case R.id.settings_checkbox_show_message_data :
                myPrefs.storeMessageDetailsSetting(checkedState);
                break;

        }
    }
}
