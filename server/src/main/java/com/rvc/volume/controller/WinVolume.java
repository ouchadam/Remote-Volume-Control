package com.rvc.volume.controller;

import com.rvc.volume.VolumeController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class WinVolume implements VolumeController {

    private static final int DEFAULT_VOLUME = 50;
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
        StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
        StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
        errorGobbler.start();
        outputGobbler.start();
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

    private class StreamGobbler extends Thread {
        private final InputStream is;
        private final String type;

        StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            String line = null;
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                while ((line = br.readLine()) != null) {
                    volume = stringVolumeToInt(line);
                }

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        private int stringVolumeToInt(String volume) {
            return (int) (Float.parseFloat(volume) * 100);
        }
    }
}
