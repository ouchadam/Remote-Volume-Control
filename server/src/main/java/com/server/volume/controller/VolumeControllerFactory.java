package com.server.volume.controller;


import com.server.util.OSHelper;
import com.server.volume.VolumeController;

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
