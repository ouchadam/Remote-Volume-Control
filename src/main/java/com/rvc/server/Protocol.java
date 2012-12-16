package com.rvc.server;

import com.rvc.volume.VolumeControl;

import java.io.IOException;

public class Protocol {

	private String exit;
	private String vol;

    private final ConnectionState server;
	
	Protocol(ConnectionState server) {
        this.server = server;
        setExitCode("0");
		this.vol = "00";
	}
	
	private synchronized void setExitCode(String exit) {
		this.exit = exit;
	}
	
	public synchronized String getExitCode() {
		return this.exit;
	}
	
    public String processInput(String input) {
    	
    	String output = "0100";
    	
    	int tempState = server.getServerRunning()? 1:0;
    	String serverState = String.valueOf(tempState);
    	
    	if (input == null) {
    		input = output;
        }

        if (input.equals("uvol")) {
        	vol = String.format("%02x", VolumeControl.getVolume());
        }
        
        if (input.equals("conn")) {
        	server.setClientConnected(true);
        }
    	
    	if (server.getClientConnected()) {
            if (input.equals("sdown")) {
                System.out.println("Client chose shutdown");
                shutDown();                           
            } 
            if (input.equals("exit")) {
            	server.setClientConnected(false);
                setExitCode("1");
                System.out.println("Client chose to disconnect");                          
            }
            if (input.substring(0, 3).equals("vol")) {
            	VolumeControl.setVolume(Integer.parseInt(input.substring(3)));
                System.out.println(input.substring(3));
            }
    	}
            
        output = getExitCode() + serverState + vol;
		return output;
    }
    
	public void shutDown() {
		if (System.getProperty("os.name").equalsIgnoreCase("Windows 7")) {
			shutdownWindows();
		} else {
			shutdownLinux();
		}		
	}
	
	public void shutdownLinux() {
		serverCommand("/sbin/halt");
	}
	
	public void shutdownWindows() {
		serverCommand(new String[] { "shutdown", "-s"});
	}
	
	public void serverCommand(String cmd) {
		this.serverCommand(new String[] { cmd });
	}
	
	public void serverCommand(final String[] cmd) {
		
		Thread t = new Thread() {
			public void run() {
				try {
					@SuppressWarnings("unused")
					Process process = Runtime.getRuntime().exec(cmd);   
				} catch (IOException ex) {
					System.err.println(ex);
				} 				
			}
		};
		t.start();
	}
}
	