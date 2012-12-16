package com.rvc.volume.controller;


import com.rvc.util.OSHelper;
import com.rvc.volume.VolumeController;

public class VolumeControllerFactory {

    public static VolumeController getVolumeController() {
        if (OSHelper.isWindows7()) {
            return new WinVolume();
        } else if (OSHelper.isOSX()) {
            return new MacVolume();
        } else {
            return new LinuxVolume();
        }
    }

}
