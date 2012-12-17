package com.rvc.volume.controller;


import com.rvc.util.OSHelper;
import com.rvc.volume.VolumeController;

public class VolumeControllerFactory {

    public static VolumeController getVolumeController(OSHelper osHelper) {
        if (osHelper.isWindows7()) {
            return new WinVolume();
        } else if (osHelper.isOSX()) {
            return new MacVolume();
        } else {
            return new LinuxVolume();
        }
    }

}
