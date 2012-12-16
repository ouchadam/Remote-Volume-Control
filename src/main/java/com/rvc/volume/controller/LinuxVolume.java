package com.rvc.volume.controller;

import com.rvc.volume.VolumeController;

import java.io.IOException;

class LinuxVolume implements VolumeController {

    @Override
    public int getVolume() {
        return 0;
    }

    @Override
    public void setVolume(int volume) {
        serverCommand("amixer -c 0 set Master " + volume);
    }

    private void serverCommand(final String cmd) {

        Thread t = new Thread() {
            public void run() {
                try {
                    @SuppressWarnings("unused")
                    Process process = Runtime.getRuntime().exec(cmd);
                } catch (IOException ex) {
                    System.err.println(ex);
                }
            }
        };
        t.start();
    }
}
