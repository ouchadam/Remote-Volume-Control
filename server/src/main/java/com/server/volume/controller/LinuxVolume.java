package com.server.volume.controller;

import com.server.volume.VolumeController;

import java.io.IOException;

class LinuxVolume implements VolumeController, GobblerCallBack {

    private int volume;

    @Override
    public int getVolume() {
        Runtime rt = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = rt.exec(new String[]{"/bin/sh", "-c", "amixer get Master | egrep -o \"[0-9]+%\""});
            startParsingResponse(proc);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            proc.waitFor();
            return volume;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return DEFAULT_VOLUME;
        }
    }

    private void startParsingResponse(Process proc) {
        StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR", this);
        StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT", this);
        errorGobbler.start();
        outputGobbler.start();
    }

    @Override
    public void onCommandFinished(String message) {
        this.volume = stringVolumeToInt(message);
    }

    private int stringVolumeToInt(String volume) {
        volume = volume.substring(0, volume.length() - 1);
        return (Integer.parseInt(volume));

    }

    @Override
    public void setVolume(int volume) {
        serverCommand("amixer -c 0 set Master " + volume);
    }

    private void serverCommand(final String cmd) {

        Thread t = new Thread() {
            public void run() {
                try {
                    Runtime.getRuntime().exec(cmd);
                } catch (IOException ex) {
                    System.err.println(ex);
                }
            }
        };
        t.start();
    }

}
