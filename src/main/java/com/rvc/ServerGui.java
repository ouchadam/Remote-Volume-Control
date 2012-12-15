package com.rvc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public class ServerGui extends JFrame {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;	
	private JFrame frame;	
	private static TrayIcon trayIcon;
	private JLabel osName, internalIp, externalIp, macAddress, port;
	
	static private JLabel status, error;
		
	private static Server server = null;
	private static boolean popUpCon = false;
	private static boolean popUpDis = false;

	ServerGui() {			
				
		new ServerSettings();	
		
		popUpCon = false;
		popUpDis = false;
		
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();               
                updateGui();       		
        		onServerExit();        		
            }
		});
	  }
	
	private static void serverExit() {
		
		System.out.println("Server Quitting");
		
		server.quit();
		
		System.exit(0);
	}
	
	private void createAndShowGUI() {
		
		final PopupMenu popup = new PopupMenu();
		trayIcon = new TrayIcon(createImage("tray.png", "tray icon"));
		SystemTray tray = SystemTray.getSystemTray();
		
		MenuItem updateItem = new MenuItem("Update");
		MenuItem exitItem = new MenuItem("Exit");
		
		final JPanel panel = new JPanel();
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		frame = new JFrame("RVC Server");
		
		osName = new JLabel("");
		internalIp = new JLabel("");
		externalIp = new JLabel("");
		macAddress = new JLabel("");
		port = new JLabel("");
		status = new JLabel("");
		error = new JLabel("Error : nothing so far...");
		
		frame.setBounds(100, 100, 250, 150);
		panel.add(osName);
		panel.add(internalIp);
		panel.add(externalIp);
		panel.add(macAddress);
		panel.add(port);	
		panel.add(status);	
		panel.add(error);	
		
		frame.add(panel);
		frame.setVisible(true);
				
		popup.add(updateItem);
		popup.add(exitItem);
		
		trayIcon.setPopupMenu(popup);
		
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }		
        
        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(true);
            }
        });
        
        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MenuItem item = (MenuItem)e.getSource();
                System.out.println(item.getLabel());
                if ("Update".equals(item.getLabel())) {
                	updateGui();               	
                } else if ("Exit".equals(item.getLabel())) {
                	serverExit();	                    
                }
            }
        };        
        
        updateItem.addActionListener(listener);
        exitItem.addActionListener(listener);       
	}
	
	protected static Image createImage(String fileName, String description) {
	    URL imageURL = ServerGui.class.getResource("/resources/" + fileName);
	     
	    if (imageURL == null) {
	        System.err.println("Resource not found: " + fileName);
	        return null;
	    } else {
	        return (new ImageIcon(imageURL, description)).getImage();
	    }
	}	
	
	private void updateGui() {		
		
		osName.setText("OS : " + System.getProperty("os.name"));
		internalIp.setText("Lan Address : " + ServerSettings.getInteralIp());
		externalIp.setText("External Address : " + ServerSettings.getExternalIp());
		macAddress.setText("Mac Address : " + ServerSettings.getMacAddress());
		port.setText("Port : " + String.valueOf(ServerSettings.getPort()));	
	}
	
	private void onServerExit() {
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {			
				//dispose();   
				//serverExit();			
				frame.setVisible(false); // whilst close to tray is checked
			}
		});	
	}	
	
	public static void clientConnected() {

		trayIcon.displayMessage("Client Connected",
                "",TrayIcon.MessageType.INFO);
		
		ServerGui.popUpCon  = true;
	}
	
	public static void clientDisconnected() {

		trayIcon.displayMessage("Client Disconnected",
                "", TrayIcon.MessageType.INFO);
		
		ServerGui.popUpCon = false;		
	}
	
	private static boolean popUpConnectShown() {
		
		return ServerGui.popUpCon;
	}
	
	protected static boolean popUpDisConnectShown() {
		return ServerGui.popUpDis;
	}
	
	
	public static void main(String args[]) {
		
		new ServerGui();

		new ServerThread();
	
		new UpdateGuiThread();
	}
	
	private static class ServerThread implements Runnable {

		ServerThread() {
			
			new Thread(this).start();			
		}
		
		@Override
		public void run() {
			
			while(!Server.getQuit()) {			
				server = new Server(ServerSettings.getPort());
				server.startServer();							
			}				
		}		
	}

	
	private static class UpdateGuiThread implements Runnable {

		UpdateGuiThread() {
			
			new Thread(this).start();			
		}
		
		@Override
		public void run() {
			
			while (!Server.getQuit()) {
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
						
				status.setText(server.getStatus());
				error.setText(server.getError());
				
				if (Server.getClientConnected() && !popUpConnectShown()) {		
					ServerGui.clientConnected();
				}
				
				if (!Server.getClientConnected() && popUpConnectShown()) {		
					ServerGui.clientDisconnected();					
				}				
			}
			
		}
		
	}
}
