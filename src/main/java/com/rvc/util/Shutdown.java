package com.rvc.util;

import java.io.IOException;

public class Shutdown {

    public void shutDown() {
        if (System.getProperty("os.name").equalsIgnoreCase("Windows 7")) {
            shutdownWindows();
        } else {
            shutdownLinux();
        }
    }

    private void shutdownLinux() {
        serverCommand("/sbin/halt");
    }

    private void shutdownWindows() {
        serverCommand(new String[] { "shutdown", "-s"});
    }

    private void serverCommand(String cmd) {
        this.serverCommand(new String[] { cmd });
    }

    private void serverCommand(final String[] cmd) {

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
