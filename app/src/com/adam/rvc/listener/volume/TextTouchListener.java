package com.adam.rvc.listener.volume;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class TextTouchListener extends VolumeListener implements View.OnTouchListener {

    private static final int THRESHOLD = 10;

    private final TextView volumeText;
    private float prevY;
    private int volume;

    public TextTouchListener(Context context, TextView textView) {
        super(context);
        this.volumeText = textView;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            int volume = motionEventToVolume(motionEvent);
            if (isValidVolume(volume)) {
                updateLocalVolume(volume);
                updateServerVolume(volume);
            }
        }
        return true;
    }

    private int motionEventToVolume(MotionEvent motionEvent) {
        if (downEvent(motionEvent)) {
            prevY = motionEvent.getRawY();
            return volume - 1;
        } else if (upEvent(motionEvent)) {
            prevY = motionEvent.getRawY();
            return volume + 1;
        } else {
            return volume;
        }
    }

    private boolean downEvent(MotionEvent motionEvent) {
        return prevY < motionEvent.getRawY() - THRESHOLD;
    }

    private boolean upEvent(MotionEvent motionEvent) {
        return prevY > motionEvent.getRawY() + THRESHOLD;
    }

    public void updateLocalVolume(int newVolume) {
        volume = newVolume;
        volumeText.setText("" + volume);
    }

    private boolean isValidVolume(int newVolume) {
        return newVolume <= 100 && newVolume >= 0 && newVolume != volume;
    }

}

