package com.adam.rvc.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import com.adam.rvc.listener.DiscoveryListener;
import com.adam.rvc.util.StatusUpdater;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;

public class DiscoveryService extends Service implements DiscoveryListener.DiscoveryCallback {

    private static final String HOSTNAME = "RVC_SERVER";
    private static final String TOUCH_ABLE_TYPE = "_my-service-type._tcp.local.";

    private final DiscoveryListener.DiscoveryCallback discoveryCallback = this;

    private WifiManager.MulticastLock mLock;
    private JmDNS zeroConf;
    private StatusUpdater statusUpdater;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        statusUpdater = new StatusUpdater(this);
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void start() throws IOException {
        statusUpdater.updateStatusAndLog("Starting");
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiinfo = wifi.getConnectionInfo();
        int intaddr = wifiinfo.getIpAddress();

        if (intaddr != 0) {
            byte[] byteaddr = new byte[] { (byte) (intaddr & 0xff), (byte) (intaddr >> 8 & 0xff),
                    (byte) (intaddr >> 16 & 0xff), (byte) (intaddr >> 24 & 0xff) };
            final InetAddress addr = InetAddress.getByAddress(byteaddr);

            statusUpdater.updateStatusAndLog(String.format("found intaddr=%d, addr=%s", intaddr, addr.toString()));
            initMulticastLock(wifi);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        zeroConf = JmDNS.create(addr, HOSTNAME);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    zeroConf.addServiceListener(TOUCH_ABLE_TYPE, new DiscoveryListener(statusUpdater, discoveryCallback));
                }
            }).start();
        }

    }

    private void initMulticastLock(WifiManager wifi) {
        mLock = wifi.createMulticastLock("RVC lock");
        mLock.acquire();
        mLock.setReferenceCounted(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void resolve(ServiceEvent serviceEvent) {
        zeroConf.requestServiceInfo(serviceEvent.getType(), serviceEvent.getName());
    }

    @Override
    public void finish(ServiceEvent serviceEvent) {
        ServiceInfo info = zeroConf.getServiceInfo(serviceEvent.getType(), serviceEvent.getName());
        statusUpdater.updateStatusAndLog("Port : " + info.getPort() +
        " " + "IP : " + info.getHostAddresses()[0]);
    }
}
