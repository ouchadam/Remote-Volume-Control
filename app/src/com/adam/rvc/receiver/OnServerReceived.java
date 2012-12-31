package com.adam.rvc.receiver;

import com.adam.rvc.server.ServerData;

public interface OnServerReceived {

    void onIpReceived(ServerData serverData);

}
