package com.adam.rvc.activity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public abstract class RVCActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
    }

    private void initWindow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

}
