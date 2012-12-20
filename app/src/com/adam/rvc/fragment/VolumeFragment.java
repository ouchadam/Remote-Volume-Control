package com.adam.rvc.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import com.adam.rvc.R;
import com.adam.rvc.listener.SeekBarListener;

public class VolumeFragment extends Fragment {

    private Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volume, container, false);
        initSeekBar(view);
        return view;
    }

    private void initSeekBar(View view) {
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.volume_seek_bar);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBarListener(context));
    }
}
