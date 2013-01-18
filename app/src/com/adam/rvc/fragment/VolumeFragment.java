package com.adam.rvc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.adam.rvc.R;
import com.adam.rvc.listener.volume.TextTouchListener;
import com.adam.rvc.receiver.VolumeUpdaterReceiver;

public class VolumeFragment extends BaseFragment implements VolumeUpdaterReceiver.OnVolumeUpdated {

    private final VolumeUpdaterReceiver volumeReceiver;

    private TextTouchListener textTouchListener;

    private ProgressBar progressBar;

    public VolumeFragment() {
        volumeReceiver = new VolumeUpdaterReceiver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(volumeReceiver, volumeReceiver.getIntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(volumeReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volume, container, false);
        initText(view);
        initProgressBar(view);
        return view;
    }

    private void initText(View view) {
        TextView volumeText = (TextView) view.findViewById(R.id.volume_text);
        textTouchListener = new TextTouchListener(context, volumeText);
        volumeText.setOnTouchListener(textTouchListener);
    }

    private void initProgressBar(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.volume_progress);
    }

    @Override
    public void onVolumeUpdate(int volume) {
        hideProgressBarIfShowing();
        textTouchListener.updateLocalVolume(volume);
    }

    private void hideProgressBarIfShowing() {
        progressBar.setVisibility(hideVisibilityIfShowing(progressBar.getVisibility()));
    }

    private int hideVisibilityIfShowing(int visibility) {
        return visibility == View.VISIBLE ? View.GONE : View.VISIBLE;
    }

}
