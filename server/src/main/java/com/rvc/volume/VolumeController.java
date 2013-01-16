package com.rvc.volume;

public interface VolumeController {

    static final int DEFAULT_VOLUME = 50;

    int getVolume();

    void setVolume(int volume);

}
