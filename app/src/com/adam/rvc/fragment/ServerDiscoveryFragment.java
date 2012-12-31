package com.adam.rvc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.adam.rvc.R;
import com.adam.rvc.receiver.OnServerReceived;
import com.adam.rvc.receiver.ServerReceiver;
import com.adam.rvc.server.ServerData;
import com.adam.rvc.service.RVCServiceFactory;

public class ServerDiscoveryFragment extends BaseFragment implements OnServerReceived {

    private final ServerReceiver serverReceiver;

    private TextView serverName;
    private TextView ipAddress;
    private TextView port;

    public ServerDiscoveryFragment() {
        serverReceiver = new ServerReceiver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(serverReceiver, serverReceiver.getIntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(serverReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ip_discovery, container, false);
        initText(view);
        return view;
    }

    private void initText(View view) {
        serverName = (TextView) view.findViewById(R.id.server_name_text);
        ipAddress = (TextView) view.findViewById(R.id.ip_address_text);
        port = (TextView) view.findViewById(R.id.port_text);
    }

    @Override
    public void onIpReceived(ServerData serverData) {
        setServerDataText(serverData);
        context.startService(RVCServiceFactory.startService(context, serverData.getIpAddress(), serverData.getPort()));
    }

    private void setServerDataText(ServerData serverData) {
        serverName.setText(serverData.getServerName());
        ipAddress.setText(serverData.getIpAddress());
        port.setText("" + serverData.getPort());
    }

}
