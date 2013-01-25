package com.server.volume.controller;

import com.server.volume.VolumeController;

import java.io.IOException;

class MacVolume implements VolumeController {

    @Override
    public int getVolume() {
        return 0;
    }

    @Override
    public void setVolume(int volume) {
        float decimalVol = (float) volume / 100;
        try {
            String cmd = "osascript -e set volw ouput volume " + Float.toString(decimalVol);
            Runtime rt = Runtime.getRuntime();
            rt.exec(new String[] {"/bin/tcsh", "-c", cmd});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
