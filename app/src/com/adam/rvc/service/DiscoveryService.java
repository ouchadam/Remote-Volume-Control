package com.adam.rvc.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import com.adam.rvc.listener.DiscoveryListener;
import com.adam.rvc.receiver.ReceiverIntentFactory;
import com.adam.rvc.server.ServerData;
import com.adam.rvc.util.StatusUpdater;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DiscoveryService extends Service implements DiscoveryListener.DiscoveryCallback {

    private static final String HOSTNAME = "RVC_SERVER";
    private static final String TOUCH_ABLE_TYPE = "_my-service-type._tcp.local.";

    private final DiscoveryListener.DiscoveryCallback discoveryCallback = this;
    private final StatusUpdater statusUpdater;

    private WifiManager.MulticastLock mLock;
    private JmDNS zeroConf;

    public DiscoveryService() {
        statusUpdater = new StatusUpdater(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startListening() throws IOException {
        statusUpdater.updateStatusAndLog("Starting");
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        int intaddr = getIntIp(wifi);
        if (addressIsValid(intaddr)) {
            initMulticastLock(wifi);
            createConnection(createInetAddress(intaddr));
        }
    }

    private int getIntIp(WifiManager wifi) {
        return wifi.getConnectionInfo().getIpAddress();
    }

    private boolean addressIsValid(int intaddr) {
        return intaddr != 0;
    }

    private void initMulticastLock(WifiManager wifi) {
        mLock = wifi.createMulticastLock("RVC lock");
        mLock.acquire();
        mLock.setReferenceCounted(true);
    }

    private InetAddress createInetAddress(int intaddr) throws UnknownHostException {
        return InetAddress.getByAddress(ipIntToByteArray(intaddr));
    }

    private byte[] ipIntToByteArray(int intaddr) {
        return new byte[]{(byte) (intaddr & 0xff), (byte) (intaddr >> 8 & 0xff),
                (byte) (intaddr >> 16 & 0xff), (byte) (intaddr >> 24 & 0xff)};
    }

    private void createConnection(final InetAddress addr) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    zeroConf = JmDNS.create(addr, HOSTNAME);
                    zeroConf.addServiceListener(TOUCH_ABLE_TYPE, new DiscoveryListener(statusUpdater, discoveryCallback));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
//        statusUpdater.updateStatusAndLog("Port : " + info.getPort() + " " + "IP : " + info.getHostAddresses()[0]);
        mLock.release();
        mLock = null;
        sendBroadcast(ReceiverIntentFactory.broadcastServerData(createServerData(serviceEvent, info)));
    }

    private ServerData createServerData(ServiceEvent serviceEvent, ServiceInfo serviceInfo) {
        return new ServerData(serviceEvent.getName(), serviceInfo.getHostAddresses()[0], serviceInfo.getPort());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        zeroConf.unregisterAllServices();
    }
}
