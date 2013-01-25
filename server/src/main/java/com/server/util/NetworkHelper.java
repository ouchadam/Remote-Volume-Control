package com.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetworkHelper {

    private static final String EXTERNAL_IP_URL = "http://api.externalip.net/ip/";
    private final String interfaceName;

    public NetworkHelper(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public InetAddress getLocalIp() {
        return new OSHelper().isLinux() ? getLinuxLocalIp() : getDefaultLocalHost();
    }

    private static InetAddress getDefaultLocalHost() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    private InetAddress getLinuxLocalIp() {
        InetAddress ip = null;
        try {
            NetworkInterface e = NetworkInterface.getByName(interfaceName);
            Enumeration<InetAddress> a = e.getInetAddresses();
            while (a.hasMoreElements()) {
                ip = a.nextElement();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }

    public String getExternalIp() throws IOException {
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

    public String getMacAddress(String internalIp) {
        String out = "";
        try {
            InetAddress address = InetAddress.getByName(internalIp);
            NetworkInterface network = NetworkInterface.getByInetAddress(address);
            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            out = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
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
