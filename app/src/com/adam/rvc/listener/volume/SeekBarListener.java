package com.adam.rvc.listener.volume;

import android.content.Context;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarListener extends VolumeListener implements SeekBar.OnSeekBarChangeListener {

    private final TextView volumeText;

    public SeekBarListener(Context context, TextView volumeText) {
        super(context);
        this.volumeText = volumeText;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        volumeText.setText("" + progress);
        updateVolume(progress);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

}
