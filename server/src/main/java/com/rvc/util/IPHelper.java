package com.rvc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;

public class IPHelper {

    public static InetAddress getLocalIp() throws UnknownHostException {
        return new OSHelper().isLinux() ? getLinuxLocalIp() : InetAddress.getLocalHost();
    }

    private static InetAddress getLinuxLocalIp() {
        InetAddress ip = null;
        try {
            Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
            NetworkInterface e = n.nextElement();
            Enumeration<InetAddress> a = e.getInetAddresses();
            for (; a.hasMoreElements(); ) {
                ip = a.nextElement();
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        return ip;
    }

    public static String getExternalIp() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(createExternalURL().openStream()));
        return in.readLine();
    }

    private static URL createExternalURL() {
        try {
            return new URL("http://api.externalip.net/ip/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
