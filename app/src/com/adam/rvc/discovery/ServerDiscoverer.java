package com.adam.rvc.discovery;

import android.content.Context;
import android.net.wifi.WifiManager;
import com.adam.rvc.receiver.ReceiverIntentFactory;
import com.adam.rvc.server.ServerData;
import com.adam.rvc.util.StatusUpdater;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerDiscoverer implements DiscoveryListener.DiscoveryCallback {

    public static final String ACTION_SERVER_DATA_RECEIVED = "com.adam.rvc.receiver.ACTION_SERVER_DATA_RECEIVED";
    public static final String EXTRA_SERVER_DATA = "com.adam.rvc.receiver.EXTRA_SERVER_DATA";

    private static final String HOSTNAME = "RVC_SERVER";
    private static final String TOUCH_ABLE_TYPE = "_my-service-type._tcp.local.";

    private final DiscoveryListener.DiscoveryCallback discoveryCallback = this;
    private final Context context;
    private final StatusUpdater statusUpdater;

    private WifiManager.MulticastLock mLock;
    private JmDNS zeroConf;

    public ServerDiscoverer(Context context, StatusUpdater statusUpdater) {
        this.context = context;
        this.statusUpdater = statusUpdater;
    }

    public void startListening() throws IOException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        statusUpdater.updateStatusAndLog("Searching...");
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
    public void resolve(ServiceEvent serviceEvent) {
        zeroConf.requestServiceInfo(serviceEvent.getType(), serviceEvent.getName());
    }

    @Override
    public void onServerDataReceived(ServiceEvent serviceEvent) {
        releaseMulticastLock();
        broadcastServerDataReceived(serviceEvent);
    }

    @Override
    public void onServerDisconnected() {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        initMulticastLock(wifi);
    }

    private void releaseMulticastLock() {
        mLock.release();
        mLock = null;
    }

    private void broadcastServerDataReceived(ServiceEvent serviceEvent) {
        context.sendBroadcast(ReceiverIntentFactory.broadcastServerData(createServerData(serviceEvent)));
    }

    private ServerData createServerData(ServiceEvent serviceEvent) {
        ServiceInfo info = zeroConf.getServiceInfo(serviceEvent.getType(), serviceEvent.getName());
        return new ServerData(serviceEvent.getName(), info.getHostAddresses()[0], info.getPort());
    }

    public void onDestroy() {
        zeroConf.unregisterAllServices();
    }

}
