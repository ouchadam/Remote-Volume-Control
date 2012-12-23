package com.adam.rvc.service;

import android.app.IntentService;
import android.content.Intent;
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

    private final StatusUpdater statusUpdater;


    private int port = 5555;
    private String iIPv4 = "192.168.0.";
    private boolean ipFound;

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
        for (int i = 1; i < 254; i++) {
            try {
                Log.log(iIPv4 + i);
                Socket mySocket = new Socket();
                SocketAddress address = new InetSocketAddress(iIPv4 + i, port);

                mySocket.connect(address, 15);

                PrintWriter out = new PrintWriter(mySocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                out.println("conn");
                out.println("check");
                String fromServer;
                while ((fromServer = in.readLine()) != null) {
                    Log.log("Server: " + fromServer);
                    if (fromServer.length() >= 4) {
                        out.println("exit");
                        ipFound = true;
                        break;
                    }
                }
            } catch (UnknownHostException e) {
            } catch (IOException e) {
            }
            if (ipFound) {
                statusUpdater.updateStatus("Server found at : " + iIPv4+i);
                return;
            }
        }
        Log.log("No server found");
    }
}
