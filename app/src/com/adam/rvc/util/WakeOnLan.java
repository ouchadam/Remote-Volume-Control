package com.adam.rvc.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class WakeOnLan {

    private static final int WAKE_PACKET_SIZE = 6;
    private static final int PORT = 9;

    public void wakeUp(String ipAddress, String macAddress) throws MalformedMacAddressException {
        byte[] bytes = createWakePacketBytes(macAddress);
        sendPacket(createWakePacket(ipAddress, bytes));
    }

    private byte[] createWakePacketBytes(String macAddress) throws MalformedMacAddressException {
        byte[] macBytes = getMacBytes(macAddress);
        byte[] bytes = new byte[WAKE_PACKET_SIZE + 16 * macBytes.length];
        for (int i = 0; i < WAKE_PACKET_SIZE; i++) {
            bytes[i] = (byte) 0xff;
        }
        for (int i = 6; i < bytes.length; i += macBytes.length) {
            System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
        }
        return bytes;
    }

    private void sendPacket(DatagramPacket packet) {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DatagramPacket createWakePacket(String ipAddress, byte[] bytes) throws MalformedMacAddressException {
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            return new DatagramPacket(bytes, bytes.length, address, PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new MalformedMacAddressException("Unknown host address.");
        }
    }

    private byte[] getMacBytes(String macStr) throws MalformedMacAddressException {
        byte[] bytes = new byte[WAKE_PACKET_SIZE];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != WAKE_PACKET_SIZE) {
            throw new MalformedMacAddressException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < WAKE_PACKET_SIZE; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw new MalformedMacAddressException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }

}
