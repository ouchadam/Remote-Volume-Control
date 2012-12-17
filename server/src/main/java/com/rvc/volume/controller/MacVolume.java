package com.rvc.volume.controller;

import com.rvc.volume.VolumeController;

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
            Process proc = rt.exec(new String[] {"/bin/tcsh", "-c", cmd});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
