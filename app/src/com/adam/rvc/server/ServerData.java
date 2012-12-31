package com.adam.rvc.server;

import android.os.Parcel;
import android.os.Parcelable;

public class ServerData implements Parcelable {

    private final String serverName;
    private final String ipAddress;
    private final int port;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ServerData createFromParcel(Parcel in) {
            return new ServerData(in);
        }

        public ServerData[] newArray(int size) {
            return new ServerData[size];
        }
    };

    public ServerData(Parcel parcel) {
        this(parcel.readString(), parcel.readString(), parcel.readInt());
    }

    public ServerData(String serverName, String ipAddress, int port) {
        this.serverName = serverName;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getServerName() {
        return serverName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(serverName);
        parcel.writeString(ipAddress);
        parcel.writeInt(port);
    }

}
