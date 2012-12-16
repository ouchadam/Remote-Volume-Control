package com.rvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class ServerSettings {
	
	private String macAddress = "";
	private String internalIp = "";
	private String externalIp = "";
	
	public ServerSettings() {
		if (getOsName().equalsIgnoreCase("Linux")) {
			setInternalIp(getLinuxIp());
		} else { 
			setInternalIp(getWindowsIp());
		}
		setMacAddress(parseMacAddress());
		parseExternalIp();					
	}	
	
	public String getLinuxIp() {
		String ip = "";
		
        Enumeration<NetworkInterface> n = null;
		try {
			n = NetworkInterface.getNetworkInterfaces();
			
            NetworkInterface e = n.nextElement();
            Enumeration<InetAddress> a = e.getInetAddresses();
            for (; a.hasMoreElements();)
            {
                    InetAddress addr = a.nextElement();              
                    ip = addr.getHostAddress();         
            }			
		} catch (SocketException e1) {
			e1.printStackTrace();
		}		
		return ip;
	}
	
	public String getWindowsIp() {
		String ip = "";
			try {
				InetAddress hostIp = InetAddress.getLocalHost();
				ip = hostIp.getHostAddress();
			} catch (Exception e) {
				// could not get IP
			}			  	
		return ip;	
	}
	
	public String parseMacAddress() {

		String out = "";
		try {			 
	 
			InetAddress address = InetAddress.getByName(getInteralIp());
			NetworkInterface network = NetworkInterface.getByInetAddress(address);
	 
			byte[] mac = network.getHardwareAddress();
	 
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
			}
			out = sb.toString();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();	 
		} catch (SocketException e){	 
			e.printStackTrace();	 
		}	
		return out;		
	}
	
	public void parseExternalIp()  {
		
		URL whatismyip;
		
		try {
			whatismyip = new URL("http://api.externalip.net/ip/");
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
				externalIp = in.readLine();
		} catch (Exception e1) {
			e1.printStackTrace();
			externalIp = "could not get";
		}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getExternalIp() {
		return externalIp;
	}
	
	public void setInternalIp(String input) {
		internalIp = input;
	}
	
	public String getInteralIp() {
		return internalIp;
	}
	
	public void setMacAddress(String input) {
		macAddress = input;
	}
	
	public String getMacAddress() {
		return macAddress;
	}
	
	public int getPort() {
		return 5555;
	}
			
	public static String getOsName() {
		return System.getProperty("os.name");
	}	
}
