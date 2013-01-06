package com.rvc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IPHelper {

    private static final String EXTERNAL_IP_URL = "http://api.externalip.net/ip/";

    public static InetAddress getLocalIp(String interfaceName) {
        return new OSHelper().isLinux() ? getLinuxLocalIp(interfaceName) : getDefaultLocalHost();
    }

    private static InetAddress getDefaultLocalHost() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static InetAddress getLinuxLocalIp(String interfaceName) {
        InetAddress ip = null;
        try {
            NetworkInterface e = NetworkInterface.getByName(interfaceName);
            Enumeration<InetAddress> a = e.getInetAddresses();
            while (a.hasMoreElements()) {
                ip = a.nextElement();
                System.out.println("Has ip : " + ip);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }

    public static String getExternalIp() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(createExternalURL().openStream()));
        return in.readLine();
    }

    private static URL createExternalURL() {
        try {
            return new URL(EXTERNAL_IP_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getAdapters() {
        Enumeration<NetworkInterface> n = getNetworkInterfaces();
        List<String> adapterNames = new ArrayList<String>();
        while (n.hasMoreElements()) {
            NetworkInterface e = n.nextElement();
            if (!addressIsLoopback(e)) {
                adapterNames.add(e.getName());
            }
        }
        return adapterNames;
    }

    private static Enumeration<NetworkInterface> getNetworkInterfaces() {
        try {
            return NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean addressIsLoopback(NetworkInterface networkInterface) {
        try {
            return networkInterface.isLoopback();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return false;
    }

}
