package com.adam.rvc;

import android.content.Context;
import android.widget.SeekBar;
import com.adam.rvc.service.RVCServiceFactory;

public class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

    private static final String VOLUME_MESSAGE_HEADER = "vol";

    private final Context context;

    public SeekBarListener(Context context) {
        this.context = context;
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        context.startService(RVCServiceFactory.writeToServer(context, createMessage(progress)));
    }

    private String createMessage(int progress) {
        return VOLUME_MESSAGE_HEADER + progress;
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

}
