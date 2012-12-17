package com.rvc.util;

public class OSHelper {

    public static boolean isWindows7() {
        return System.getProperty("os.name").equalsIgnoreCase("Windows 7");
    }

    public static boolean isOSX() {
        return System.getProperty("os.name").equalsIgnoreCase("MAC OS X");
    }

    public static boolean isLinux() {
        return System.getProperty("os.name").equalsIgnoreCase("Linux");
    }

}
