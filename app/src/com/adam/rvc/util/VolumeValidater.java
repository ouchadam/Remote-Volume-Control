package com.adam.rvc.util;

public class VolumeValidater {

    private static final int MIN_VOLUME = 0;
    private static final int MAX_VOLUME = 100;

    public boolean validate(int volume) {
        return volume >= MIN_VOLUME && volume <= MAX_VOLUME;
    }
}
