package com.adam.rvc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import com.adam.rvc.R;
import com.adam.rvc.listener.volume.TextListener;

public class VolumeFragment extends BaseFragment {

    private static final int SEEK_BAR_MAX = 100;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volume, container, false);
        initText(view);
//        initSeekBar(view);
        return view;
    }

    private void initText(View view) {
        TextView volumeText = (TextView) view.findViewById(R.id.volume_text);
        volumeText.setText("0");
        volumeText.setOnTouchListener(new TextListener(context, volumeText));
    }

    private void initSeekBar(View view) {
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.volume_seek_bar);
        seekBar.setMax(SEEK_BAR_MAX);
//        seekBar.setOnSeekBarChangeListener(new SeekBarListener(context, volumeText));
    }

}
