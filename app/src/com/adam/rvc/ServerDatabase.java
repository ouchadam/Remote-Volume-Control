package com.adam.rvc;



public class ServerDatabase {
	
	private String ipAddress; 
	private String port;
	private String serverName;
	private String macAddress;
	private boolean isActive;
	
	private static String IPADDRESS = "";
	private static int PORT = 0;
	private static String MACADDRESS = "";
	
	ServerDatabase() {		
		isActive = false;
	}
	
	public void setServerName(String input) {		
		if (input != null)
			serverName = input;		
	}
	public void setIpAddress(String input) {		
		if (input != null)
			ipAddress = input;		
	}

	public void setPort(String input) {	
		if (!input.equals("") || input != null )
			port = input;
	}
	
	public String getServerName() {
		return serverName;
	}
	
	public String getIpAddress() {
			return ipAddress;			
	}
	
	public String getPort() {
		if(port.trim().length() != 0) {
			return port;
		} else {
			return "0";
		}
	}
	
	public void setActive(boolean input) {
		isActive = input;
	}
	
	public boolean getActive() {
		return isActive;
	}
		
	public void setMacAddress(String input) {
			macAddress = input;
	}
	
	public String getMacAddress() {
		if(macAddress.length() == 17) {
			return macAddress;
		}else { 
			return "                 ";
		}
	}
	
	public static String getSelectedIp() {
		return IPADDRESS;
	}
	
	public static int getSelectedPort() {
		return PORT;
	}
	
	public static void setSelectedIp(String input) {
		if (input != null)
			IPADDRESS = input;
	}
	
	public static void setSelectedPort(String input) {
		if(input.trim().length() != 0) {
			PORT = Integer.valueOf(input);
		}
	}	
	
	public static void setSelectedMac(String input) {
		if (input != null)
			MACADDRESS = input;
	}
	
	public static String getSelectedMac() {
		return MACADDRESS;
	}
	
}
