package com.server.volume.controller;

import com.server.volume.VolumeController;

import java.io.IOException;

class WinVolume implements VolumeController, GobblerCallBack {

    private static final int COMMAND_LENGTH = 3;

    private int volume;

    @Override
    public int getVolume() {
        Runtime rt = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = rt.exec(createGetVolumeCmd());
            startParsingResponse(proc);
        } catch (IOException e1) {
            e1.printStackTrace();
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
        this.volume = (int) (Float.parseFloat(message) * 100);
    }

    @Override
    public void setVolume(int volume) {
        float decimalVol = (float) volume / 100;
        try {
            Runtime rt = Runtime.getRuntime();
            rt.exec(createSetVolumeCmd(decimalVol));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] createGetVolumeCmd() {
        String[] cmd = createBaseCmd();
        cmd[COMMAND_LENGTH] = "vol";
        return cmd;
    }

    private String[] createSetVolumeCmd(float volume) {
        String[] cmd = createBaseCmd();
        cmd[COMMAND_LENGTH] = Float.toString(volume);
        return cmd;
    }

    private String[] createBaseCmd() {
        String[] cmd = new String[4];
        cmd[0] = "cmd.exe";
        cmd[1] = "/C";
        cmd[2] = "CoreAudioApi.exe";
        return cmd;
    }

}
