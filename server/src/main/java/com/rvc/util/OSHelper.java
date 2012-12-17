package com.rvc.util;

public class OSHelper {

    public boolean isWindows7() {
        return System.getProperty("os.name").equalsIgnoreCase("Windows 7");
    }

    public boolean isOSX() {
        return System.getProperty("os.name").equalsIgnoreCase("MAC OS X");
    }

    public boolean isLinux() {
        return System.getProperty("os.name").equalsIgnoreCase("Linux");
    }

}
