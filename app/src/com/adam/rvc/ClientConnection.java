package com.adam.rvc;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection {

	private final Socket socket;
	private PrintWriter output;
	private BufferedReader input;	
	private boolean connecting;
	private int currentvolume = 0;
	
	private String fromServer;

    public ClientConnection(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        output = new PrintWriter(socket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

	public void connectToServer() {
        confirmConnection();
	}

	private void confirmConnection() {
		for (int i = 0; i < 100; i ++) {
			if (socket.isConnected()) {
				writeToServer("conn");
				writeToServer("uvol");
				break;
			} else {

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
		}		
	}
	
	public String getFromServer() {
		return fromServer;
	}
	
	private void setFromServer(String fromServer) {
		this.fromServer = fromServer;
	}
	
	public void checkServerState() {
		if (socket.isConnected()) {
			output.println("check");
			try {
				setFromServer(input.readLine());
				Log.d("adam", "Packet : " + getFromServer());
                if (getFromServer().substring(1, 2).equals("1")) {
                    closeSocket();
                }
				setCurrentvolume(Integer.parseInt(getFromServer().substring(2, 4), 16));
			} catch (Exception e) {
				closeSocket();
			}
		}
	}

	public void writeToServer(String fromUser) {		
		if (socket.isConnected()) {
			if (fromUser != null) {
				output.println(fromUser);
            }
		}
	}

	public void closeSocket() {
		if (socket.isConnected()) {
			try {
				output.flush();
				input.close();
				output.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();				
			}			
		}
	}
	
	public boolean tryingToConnect() {
		return this.connecting;
	}
	
	public void tryingToConnect(boolean input) {
		this.connecting = input;
	}

	public int getCurrentvolume() {
		return this.currentvolume;
	}

	public void setCurrentvolume(int currentvolume) {
		
		this.currentvolume = currentvolume;
	}
	
}
