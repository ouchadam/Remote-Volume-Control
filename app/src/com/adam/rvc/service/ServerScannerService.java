package com.adam.rvc.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import com.adam.rvc.util.Log;
import com.adam.rvc.util.StatusUpdater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class ServerScannerService extends IntentService {

    private static final int IP_RANGE = 254;
    private static final int SOCKET_CONNECTION_TIMEOUT = 15;
    private final StatusUpdater statusUpdater;

    private int port = 5555;
    private PrintWriter out;
    private BufferedReader in;
    private Socket serverScanSocket;

    public ServerScannerService() {
        this("ServerScannerService");
    }

    public ServerScannerService(String name) {
        super(name);
        statusUpdater = new StatusUpdater(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        findServer();
    }

    private void findServer() {
        String subnet = getLocalSubnet();
        for (int i = 1; i < IP_RANGE; i++) {
            String ip = subnet + i;
            Log.log(ip);
            try {
                serverScanSocket = connectToIp(new Socket(), ip);
                initIO();
                out.println("check");
                if (serverResponse(ip)) {
                    closeConnection();
                    return;
                }
                closeConnection();
            } catch (UnknownHostException e) {
            } catch (IOException e) {
            }


        }
        statusUpdater.updateStatusAndLog("No server found");
    }

    private String getLocalSubnet() {
        DhcpInfo networkInfo = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).getDhcpInfo();
        String fullIp = intToIp(networkInfo.ipAddress);
        return fullIp.substring(0, fullIp.lastIndexOf(".") + 1);
    }

    private String intToIp(int addr) {
        return  ((addr & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF));
    }

    private Socket connectToIp(Socket socket, String ip) throws IOException {
        socket.connect(createSocketAddress(port, ip), SOCKET_CONNECTION_TIMEOUT);
        return socket;
    }

    private SocketAddress createSocketAddress(int port, String ip) {
        return new InetSocketAddress(ip, port);
    }

    private void initIO() throws IOException {
        out = new PrintWriter(serverScanSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(serverScanSocket.getInputStream()));
    }

    private boolean serverResponse(String ip) throws IOException {
        String fromServer;
        while ((fromServer = in.readLine()) != null) {
            Log.log("Server: " + fromServer);
            if (fromServer.length() >= 4) {
                onIpFound(ip);
                return true;
            }
        }
        return false;
    }

    private void onIpFound(String ip) {
        out.println("exit");
        statusUpdater.updateStatus("Server found at : " + ip);
//        startService(RVCServiceFactory.startService(this, ip, port));
    }

    private void closeConnection() throws IOException {
        out.close();
        in.close();
        serverScanSocket.close();
    }
}
