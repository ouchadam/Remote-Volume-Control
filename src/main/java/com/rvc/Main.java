package com.rvc;

public class Main {

    public static void main(String args[]) {
        ServerGui serverGui = new ServerGui();
        serverGui.startServer();
        serverGui.startGuiUpdates();
    }

}
