package com.adam.rvc.listener;

import android.content.Context;
import android.widget.SeekBar;

public class SeekBarListener extends VolumeListener implements SeekBar.OnSeekBarChangeListener {

    public SeekBarListener(Context context) {
        super(context);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        updateVolume(progress);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

}
