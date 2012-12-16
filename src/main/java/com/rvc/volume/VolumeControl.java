package com.rvc.volume;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class VolumeControl
{
	private static String volume;
	
	public synchronized static void setServerVolume(String serverVolume) {
		VolumeControl.volume = serverVolume;
	}
	
	public synchronized static int getServerVolume() {
		
		return (int) (Float.parseFloat(VolumeControl.volume) * 100);	
	}
	
	static class StreamGobbler extends Thread
	{
	    InputStream is;
	    String type;
	    
	    StreamGobbler(InputStream is, String type)
	    {
	        this.is = is;
	        this.type = type;
	    }
	    
	    public  void run()
	    {
	    	String line = null;
	    	
	        try
	        {
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            
	            while ( (line = br.readLine()) != null) {
	                //System.out.println("volume: " + line);
	                setServerVolume(line);
	            }
	            
	            } catch (IOException ioe)
	              {
	                ioe.printStackTrace();  
	              }	     
	    }
	}
	
	
	public static int getVolume() {
		
		if (System.getProperty("os.name").equalsIgnoreCase("Windows 7")) {
		
		    String[] cmd = new String[4];
	
		    cmd[0] = "cmd.exe" ;
		    cmd[1] = "/C";
		    cmd[2] = "CoreAudioApi.exe";
		    cmd[3] = "vol";
	
		    Runtime rt = Runtime.getRuntime();
		    Process proc = null;
			try {
				proc = rt.exec(cmd);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	
		   StreamGobbler errorGobbler = new 
		        StreamGobbler(proc.getErrorStream(), "ERROR");            
		    
		    StreamGobbler outputGobbler = new 
		        StreamGobbler(proc.getInputStream(), "OUTPUT");
		        
		    errorGobbler.start();
		    outputGobbler.start();
		                            
	
		    try {
				@SuppressWarnings("unused")
				int exitVal = proc.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}  
	
		    return getServerVolume();
		} else {
			return 0;
		}
		

	}
	
	@SuppressWarnings("unused")
	public static void setVolume(int newVolume) {
		
		float decimalVol = (float) newVolume / 100;	
		
		if (System.getProperty("os.name").equalsIgnoreCase("Windows 7")) {
			
		    try {
		    	
		    	Runtime rt = Runtime.getRuntime();
		    	
				
			    String[] cmd = new String[4];
		
			    cmd[0] = "cmd.exe" ;
			    cmd[1] = "/C";
			    cmd[2] = "CoreAudioApi.exe";
			    cmd[3] = Float.toString(decimalVol);	   
		    	
				Process proc = rt.exec(cmd);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (System.getProperty("os.name").equalsIgnoreCase("MAC OS X")) {
			
		    try {

			    String cmd = "osascript -e set volw ouput volume " + Float.toString(decimalVol);	
			    		    		 
			    Runtime rt = Runtime.getRuntime();
		    
				Process proc = rt.exec(new String[] {"/bin/tcsh", "-c", cmd});
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
		
    public static void main(String args[])
    {
    	String currentVolume = String.valueOf(getVolume());	
    	System.out.println(currentVolume);
		setVolume(60);

    }
}