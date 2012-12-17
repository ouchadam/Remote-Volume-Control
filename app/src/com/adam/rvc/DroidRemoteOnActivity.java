package com.adam.rvc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class DroidRemoteOnActivity extends Activity implements OnClickListener  {
		
	private final static int CONNECT = 0;
	private final static int WRITE = 1;
	private final static int EXIT = 2;
	private final static int WAKE = 3;
		
	private ClientConnection client;
	private Handler handle;
	private TextView text;
	private ProgressBar pBar;
	private SeekBar sBar;
	
	private boolean volumeSet = false;
	
	private String toServer = "";
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connect:
                new ServerTask().execute(CONNECT);
                return true;               
                
            case R.id.iplist:
            	Intent intent = new Intent(DroidRemoteOnActivity.this, ServerList.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);      

        text = (TextView) findViewById(R.id.text);       
        pBar = (ProgressBar) findViewById(R.id.progressBar1);
        sBar = (SeekBar) findViewById(R.id.seekBar1);
        
        sBar.setMax(100);        
        sBar.setOnSeekBarChangeListener(new seekListener());
        
        Button button = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);    
        
        button.setOnClickListener(this);
        button2.setOnClickListener(this);   
    }
	
	@Override
	public void onResume() {
	
		getDefaultPrefs();		
		init();
		
		new ServerTask().execute(CONNECT);
		
		handle.postDelayed(timerTask, 10);   							
		
		super.onResume();
		
	}
	
	private void getDefaultPrefs() {
		
		SharedPreferences appSharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this.getApplicationContext());
		
		try {		
			ServerDatabase.setSelectedIp(appSharedPrefs.getString("SelectedServerIp", ""));
			ServerDatabase.setSelectedPort(appSharedPrefs.getString("SelectedServerPort", ""));
			ServerDatabase.setSelectedMac(appSharedPrefs.getString("SelectedServerMac", ""));
		} catch (Exception e) {
			// skip initial connect true
		} // first run = nulls
		
		Log.d("adam","IP : " + ServerDatabase.getSelectedIp());
		Log.d("adam","Port : " + ServerDatabase.getSelectedPort());
		
	}
		
	private void init() {

        try {
            client = new ClientConnection(ServerDatabase.getSelectedIp(), ServerDatabase.getSelectedPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
        handle = new Handler();
	}
	
    class seekListener implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromUser) {
        	
			writeToServer("vol" + progress);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}

    }
	
	public void onClick(View v) {		
			
		switch(v.getId()) {	
		
		case R.id.button1:
			if(client != null) {
				writeToServer("sdown");
			}
			break;
		case R.id.button2:
				new ServerTask().execute(WAKE);
			break;		
		}
	}
    
	
    private Runnable timerTask = new Runnable() {
	
		public void run() {
			long startTime = System.currentTimeMillis();
			
			if (!sBar.isPressed()) {			
				new CheckServerStateTask().execute();	
				setConnectedText();
			}
			
			//Log.d("adam", "Are sockets connected? : " + String.valueOf(client.isSocketConnected() +
			//		" || " + "is server connectable? : " + String.valueOf(client.isServerConnectable())));
			
			long delayTime = 2000;
			
			if (volumeSet) {
				delayTime = 2000;
			} else {
				delayTime = 250;
			}
			
			handle.postAtTime(this, startTime);
			handle.removeCallbacks(this);
			handle.postDelayed(this, delayTime);		
		}
    };
    
    public void setConnectedText() {
		
    	pBar.setVisibility(View.INVISIBLE);
    	
        if(client != null) {
        	client.tryingToConnect(false);
        	text.setText(this.getString(R.string.connected));
        }else {
        	if (client.tryingToConnect()) {
        		text.setText(this.getString(R.string.connecting));
        		pBar.setVisibility(View.VISIBLE);
        		serverTimeout();
        	} else {
        		
        	text.setText(this.getString(R.string.notconnected));
        	}
        }
    }
		
    public void serverTimeout() {   	
    	new Thread(new Runnable() {
			
			public void run() {
		    	try {
					Thread.sleep(5000);
					
					setSbarVolume();
					client.tryingToConnect(false);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			}
		}).start();
	}
    
    public void getServerVolume() {   	
    	new Thread(new Runnable() {			

			public void run() {
		    	try {
		    		
		    		volumeSet = false;
		    		
		    		for (int i = 0; i < 10; i ++) {
		    			
		    			Log.d("adam", "Volume is : " + client.getCurrentvolume());
		    			
		    			if (client.getCurrentvolume() != 00) {
		    				setSbarVolume();
		    				volumeSet = true;
		    				break;
		    				
		    			} else {
		    				Thread.sleep(1000);
		    			}
		    		}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			}
		}).start();
	}
    

	public void setSbarVolume() {
		sBar.setProgress(client.getCurrentvolume());
		//Log.d("adam", String.valueOf(client.getCurrentvolume()));
	}
		
	private void writeToServer(String toServer) {
		this.toServer = toServer;
		new ServerTask().execute(WRITE);
	}
	
	private class ServerTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... index) {
			
			switch(index[0]) {			
				case CONNECT: 
					if(!client.tryingToConnect()) {
						client.tryingToConnect(true);
						client.connectToServer();
						getServerVolume();
					}																		
					break;
				case WRITE:
					if (client != null)
						client.writeToServer(toServer);				
					break;
				case EXIT:
						client.writeToServer("exit");
						client.closeSocket();
						Log.d("adam", "App Exit");
					break;
				case WAKE:
					WakeOnLan.sendPacket(ServerDatabase.getSelectedIp(), ServerDatabase.getSelectedMac());
					break;
			}		
			return 1;		
		}							
		
	}
	
	private class CheckServerStateTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			
			client.checkServerState();	
			return null;
		}
	}
	
	@Override
	public void onPause() {
		
		//saveDefaultPrefs();
		
		try {
			new ServerTask().execute(EXIT);
		} catch (Exception e) {
			
		}	
		
		handle.removeCallbacks(timerTask);
		
		super.onPause();
	}		

}