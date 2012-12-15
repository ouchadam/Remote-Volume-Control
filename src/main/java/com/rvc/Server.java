package com.rvc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;

public class Server {

	private static boolean serverQuit, serverRunning;
	
	private boolean ioOpened, socketsOpened;
	
	private static boolean clientConnectedPopUp;
	private boolean clientDisconnectedPopUp;
	
	private String fromClient;
		
	private int port;
	private String status, error;
	
	Server(int port) {	
		
		setPort(port);
		setServerRunning(false);
		
		error = "Error : nothing so far...";

		serverQuit = false;
		
		clientConnectedPopUp = false; 
		clientDisconnectedPopUp = false;
		
		ioOpened = false;
		socketsOpened = false;
		
		fromClient = "0100";
		serverRunning = false;
	}

	private void setPort(int port) {
		this.port = port;	
	}
	
	private int getPort() {
		return this.port;
	}

	public void startServer() {

		System.out.println("Server start");	
		setStatus("Server start");
		
		Socket clientSocket = null;
		ServerSocket serverSocket = null;
		
		serverSocket = createServerSocket(serverSocket, getPort());
		clientSocket = createClientSocket(clientSocket, serverSocket);						
		
		PrintWriter out = null;
		BufferedReader in = null;
		
		out = createOutputIO(out, clientSocket);
		in = createInputIO(in, clientSocket);
		
		ReadSocket readSocket = new ReadSocket(in);
		new WriteToClient(out);	
		new ClientTimeout();
		
		readSocket.join();

		setClientDisconnectedPopUp(true);
		
		closeSockets(clientSocket, serverSocket);
		closeIO(in, out);
		
		if (Server.getQuit())
			System.exit(1);
				
	}
	
	private ServerSocket createServerSocket(ServerSocket serverSocket, int port){

		try {
			serverSocket = new ServerSocket(port);
			
		} catch (IOException e) {
			
			System.out.println("couldn't connect : IOException || is the server already running?");
			System.exit(1);		
		}

		return serverSocket;		
	}
	
	private Socket createClientSocket(Socket clientSocket, ServerSocket serverSocket) {	
				
		System.out.println("Waiting for client");
		setStatus("Waiting for client");
			
		try {
			clientSocket = serverSocket.accept();
			
			System.out.println("Client Socket Connected");
			setStatus("Client Socket Conn");
			
			socketsOpened = true;
			setServerRunning(socketsOpened);
			
		} catch (IOException e) {
			
			System.out.println("Could not connect");
			
			e.printStackTrace();
		}
	
		return clientSocket;		
	}
		
	
	private PrintWriter createOutputIO(PrintWriter out, Socket clientSocket) {
		
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
			setError("Error : IO failed");
			setServerRunning(false);
		}
		return out;
	}
	
	private BufferedReader createInputIO(BufferedReader in, Socket clientSocket) {
		
		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));	
			
			ioOpened = true;
			
		} catch (IOException e) {
			e.printStackTrace();
			setError("Error : IO failed");
			setServerRunning(false);
		}
		return in;
	}
	
	private class ReadSocket implements Runnable {
	
			Thread readClientResponse;
			BufferedReader in;
			
			
			ReadSocket(BufferedReader in) {
				this.in = in;
				readClientResponse = new Thread(this);
				readClientResponse.start();
			}
						
			@Override
			public void run() {
				
				Protocol protocol = new Protocol();
				
				while(getServerRunning()) {																	
					
					if (socketsOpened) {
					
						try {
							setFromClient(protocol.processInput(in.readLine()));
							
						} catch (Exception e) {
							
							setError("Error : checkClient thread");
							
							saveException(e);
							
							setServerRunning(false);							
							}				
					}
					
					if (getFromClient().substring(0,1).equals("1"))  {
						setError("Error : Client Quit");
						setServerRunning(false);		    
					}
				}			
			}	
			
			public void join() {
				try {
					this.readClientResponse.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
	}
	


	private class WriteToClient implements Runnable {
		
		PrintWriter out;
		
		WriteToClient(PrintWriter out) {
			
			this.out = out;
			new Thread(this).start();
		}
		
		public void run() {
			
			while(getServerRunning()) {			
				
				if (socketsOpened) {
					
					if (getFromClient().substring(1, 2).equals("1")) {

						out.println(getFromClient());	
					}

					if (getFromClient().substring(0,1).equals("1"))  {
						setError("Error : Client Quit");						
						setServerRunning(false);
					}
				}			
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}			
	}
	
	private class ClientTimeout implements Runnable {	
				
		public ClientTimeout() {
			new Thread(this).start();
		}							
		
		public void run() {
			
			try {
				for (int i = 10; i > 0; i --) {
					
					if (Server.getClientConnected()) {
						
						System.out.println("\nClient Connection Confirmed");
						setStatus("Client Connected");
						break;
						
					} else {
						System.out.print('\r'+"TIMEOUT : " + i);
						setStatus("Timeout : " + i);
						Thread.sleep(1000);
					}
				}
				if (getServerRunning()) {
					
					if (!Server.getClientConnected()) {
						System.out.println("\nClient Timed Out");
						setServerRunning(false);
					} 
				}
				
			} catch (InterruptedException e) {
				//e.printStackTrace();
				setError("Error : Timeout IE");}				
		}					
	}
		
	public void closeIO(BufferedReader in, PrintWriter out) {
		
		if (ioOpened) {					

			try {
				out.flush();			
				out.close();
				in.close();
				
				this.ioOpened = false;
				
				System.out.println("IO closed");
				
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}		
	}
	
	public void closeSockets(Socket clientSocket, ServerSocket serverSocket) {
		
		if (socketsOpened) {
			
			try {
				serverSocket.close();
				clientSocket.close();
				
				System.out.println("Sockets closed");
				
				this.socketsOpened = false;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}		
	
	protected void saveException(Exception e) {
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String s = sw.toString(); // stack trace as a string
		
		Calendar cal = Calendar.getInstance();

		try {
			BufferedWriter file = new BufferedWriter(new FileWriter("log" + cal.getTime() +".txt"));
			file.write(s);
			file.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	private void setError(String error) {
		this.error = error;
		
	}
	
	public String getError() {
		return this.error;
	}
	
	
	public synchronized void quit() {
		setServerRunning(false);
		Server.serverQuit = true;	
	}
	
	public synchronized static boolean getQuit() {
		return Server.serverQuit;
	}
	
	public synchronized void setFromClient(String input) {
		this.fromClient = input;
	}
	
	public synchronized String getFromClient() {
		return this.fromClient;
	}

	public static boolean getClientConnected() {
		return Server.clientConnectedPopUp;
	}

	public static void setClientConnected(boolean clientConnected) {
		Server.clientConnectedPopUp = clientConnected;
	}

	public boolean getClientDisconnectedPopUp() {
		return clientDisconnectedPopUp;
	}

	public void setClientDisconnectedPopUp(boolean b) {
		this.clientDisconnectedPopUp = b;
		
	}

	public String getStatus() {
		return status;
	}
	
	private void setStatus(String status) {
		this.status = status;
	}	
	
	private synchronized void setServerRunning(boolean serverRunning) {
		Server.serverRunning = serverRunning;		
	}
	
	public static synchronized boolean getServerRunning() {
		return serverRunning;
	}
	
}
