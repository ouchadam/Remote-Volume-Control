package com.rvc;

import java.io.IOException;

public class Protocol {

	private String exit;
	private String vol;
	
	Protocol() {
		setExitCode("0");
		this.vol = "00";
	}
	
	private synchronized void setExitCode(String exit) {
		this.exit = exit;
	}
	
	public synchronized String getExitCode() {
		return this.exit;
	}
	
    public String processInput(String theInput) {
    	
    	String theOutput = "0100";
    	
    	int tempState = Server.getServerRunning()? 1:0;
    	String serverState = String.valueOf(tempState);
    	
    	//System.out.println("TO CLIENT System Volume: " +  Integer.parseInt(vol, 16));
    	//System.out.println("TO CLIENT Server State: " + tempState);
    	
    	if (theInput == null)
    		theInput = theOutput;
    	
    	//if (!theInput.equals("check"))
    	//	System.out.println(theInput);
    	
    	if (theInput.equalsIgnoreCase("check")) {
            //System.out.println("Client sent check");               
    	}
	
        if (theInput.equals("uvol")) {
        	vol = String.format("%02x", VolumeControl.getVolume());
        }
        
        if (theInput.equals("conn")) {
        	Server.setClientConnected(true);
        }
    	
    	if (Server.getClientConnected()) {
    	
            if (theInput.equals("sdown")) {
                System.out.println("Client chose shutdown");
                shutDown();                           
            } 
            if (theInput.equals("exit")) {
            	Server.setClientConnected(false);
                setExitCode("1");
                System.out.println("Client chose to disconnect");                          
            }

            if (theInput.substring(0, 3).equals("vol")) {
            	VolumeControl.setVolume(Integer.parseInt(theInput.substring(3)));
            	
            }
    	}
            
        theOutput = getExitCode() + serverState + vol;  
        //System.out.println(theOutput); 
    	   	
		return theOutput;
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
	