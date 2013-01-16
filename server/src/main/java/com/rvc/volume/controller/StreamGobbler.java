package com.rvc.volume.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {

    private final InputStream is;
    private final String type;
    private final GobblerCallBack callback;

    StreamGobbler(InputStream is, String type, GobblerCallBack callBack) {
        this.is = is;
        this.type = type;
        this.callback = callBack;
    }

    public void run() {
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                callback.onCommandFinished(line);
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
