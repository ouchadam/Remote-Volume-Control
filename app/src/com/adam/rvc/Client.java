package com.adam.rvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.util.Log;

public class Client  {

	private Socket socket;
	private PrintWriter output;
	private BufferedReader input;	

	private boolean serverState;
	private boolean socketsConnected;
	private boolean connecting;
	
	private int currentvolume;
	
	private static String fromServer;

	Client()  {  
		
		currentvolume = 0;
		
		socket = null;
		output = null;
		input = null;
		
		serverState = false;
		socketsConnected = false;
		connecting = false;
		
	}

	public void connectToServer(String ip, int port) {

		try {
			socket = new Socket(ip,port);
			output = new PrintWriter(socket.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			socketsConnected = true;							
			
			confirmConnection();			
			
		} catch (Exception e) {	
			
			socketsConnected = false;     	
		}  				
	}

	private void confirmConnection() throws InterruptedException {
		for (int i = 0; i < 100; i ++) {
			
			//Log.d("adam", "Trying to connect");
			
			if (isServerConnectable()) {
				
				writeToServer("conn");
				writeToServer("uvol");
				
				break;
				
			} else {
			
				Thread.sleep(100);
			}
		}		
	}

	public synchronized boolean isSocketConnected() {
		return this.socketsConnected;
	}
	
	public synchronized boolean isServerConnectable() {
		return this.serverState;
	}
	
	public synchronized String getFromServer() {
		return Client.fromServer;
	}
	
	private synchronized static void setFromServer(String fromServer) {
		Client.fromServer = fromServer;
	}
	
	public void checkServerState() {
				
		if (socketsConnected) {

			output.println("check");
			
			try {     	     
				
				setFromServer(input.readLine());
				
				Log.d("adam", "Packet : " + getFromServer());
				
				setServerState(getFromServer().substring(1, 2).equals("1"));	    
				setCurrentvolume(Integer.parseInt(getFromServer().substring(2, 4), 16));	 


			} catch (Exception e) {
				closeSocket();
			}
		}
	}

	public void writeToServer(String fromUser) {		

		if (socketsConnected) {    
			
			if (fromUser != null) 
				output.println(fromUser);	
		}
	}

	private synchronized void setServerState(boolean serverState) {
		
		this.serverState = serverState;		
	}

	public void closeSocket() {

		setServerState(false);
		
		if (socketsConnected) {
			
			try {
				output.flush();
				input.close();
				output.close();
				socket.close();

				socketsConnected = false;
				
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
