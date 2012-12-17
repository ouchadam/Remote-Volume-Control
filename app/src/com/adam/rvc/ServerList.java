package com.adam.rvc;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ServerList extends ListActivity implements OnItemClickListener {

	private String[] storedServers = { "" };
	private ListView lv;
	private ArrayAdapter<String> adapter;
	
	private EditText serverName, ipAddress, port, 
				mac_1, mac_2, mac_3, mac_4, mac_5, mac_6;

	ArrayList<ServerDatabase> listOfServers = new ArrayList<ServerDatabase>();

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.server_list);

		lv = getListView();
		lv.setOnItemClickListener(this);					
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		reStore();
		
		longClickListener();
		buttonListener();
	}
	
	public void reStore() {
		
		SharedPreferences appSharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this.getApplicationContext());
		
		Gson gson = new Gson();
		String json = appSharedPrefs.getString("ServerDatabase", "");

		Type collectionType = new TypeToken<Collection<ServerDatabase>>() {}.getType();
		if (collectionType != null) {
			Collection<ServerDatabase> ints2 = gson.fromJson(json,collectionType);

			if (ints2 != null) {
				listOfServers.clear();
				listOfServers.addAll(ints2);

				setList();
				restoreSelectedServer();
			}
		}
	}
	
	public void setList() {		
		
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,
				addServerNamesToArray());

		setListAdapter(adapter);
		
		for (int i = 0; i < listOfServers.size(); i ++) {
			if (listOfServers.get(i).getActive()) {
				lv.setItemChecked(i, true);
				storeDefault(i);
			}
		}
	}	
	
	public String[] addServerNamesToArray() {

		int listSize = listOfServers.size();
		storedServers = new String[listSize];

		for (int i = 0; i < listSize; i++) {
			storedServers[i] = getServerName(listOfServers.get(i));
		}
		return storedServers;
	}
	
	public String getServerName(ServerDatabase a) {
		String name;
		name = a.getServerName();

		return name;
	}

	public void restoreSelectedServer() {
		
		ServerDatabase a;
		
		  for (int i = 0; i < listOfServers.size(); i++) {
			  
			  a = listOfServers.get(i);
			  
				if (a.getActive()) {
					
					lv.setItemChecked(i, true);
					adapter.notifyDataSetChanged();
				}	
		  }			
	}
	
	private void longClickListener() {
		
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				
				editServerAlert(listOfServers.get(position), position).show();
				store();
				
				return false;
			}
		});
	}
	

	private void buttonListener() {
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				addNewServerAlert().show();
				store();
			}
		});
	}
	
	public void store() {
		
		List<? extends ServerDatabase> col = new ArrayList<ServerDatabase>(listOfServers);

		SharedPreferences appSharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this.getApplicationContext());
		Editor prefsEditor = appSharedPrefs.edit();
		Gson gson = new Gson();

		String json = gson.toJson(col);
		prefsEditor.putString("ServerDatabase", json);
		prefsEditor.commit();	
	}
	
	public AlertDialog addNewServerAlert() {

		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
		return new AlertDialog.Builder(ServerList.this)
		.setTitle("Add new server")
		.setView(textEntryView)
		.setPositiveButton("Save",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int whichButton) {

				linkTextViews(textEntryView);
				saveStringsToDatabase(true);		
				setList();
				store();
			}
		})
		.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int whichButton) {
			}
		}).create();
	}
	
	public AlertDialog editServerAlert(ServerDatabase serverDatabase, final int position) {

		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);

		linkTextViews(textEntryView);
		populateTextViews(textEntryView, serverDatabase);

		return new AlertDialog.Builder(ServerList.this)
		.setTitle("Edit Server")
		.setView(textEntryView)
		.setPositiveButton("Save",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int whichButton) {

				saveStringsToDatabase(position,false);
				setList();
				store();
			}
		})
		.setNegativeButton("Remove",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
					int whichButton) {					
						
						listOfServers.remove(position);
						setList();
						store();
			}
		}).create();
	}
	
	public void linkTextViews(View textEntryView) {
		
		serverName = (EditText) textEntryView.findViewById(R.id.serverName);
		ipAddress = (EditText) textEntryView.findViewById(R.id.ipAddress);
		port = (EditText) textEntryView.findViewById(R.id.port);
		mac_1 = (EditText) textEntryView.findViewById(R.id.mac_1);
		mac_2 = (EditText) textEntryView.findViewById(R.id.mac_2);
		mac_3 = (EditText) textEntryView.findViewById(R.id.mac_3);
		mac_4 = (EditText) textEntryView.findViewById(R.id.mac_4);
		mac_5 = (EditText) textEntryView.findViewById(R.id.mac_5);
		mac_6 = (EditText) textEntryView.findViewById(R.id.mac_6);
		
	}
	
	public void populateTextViews(View textEntryView, ServerDatabase s) {
		
		serverName.setText(s.getServerName());
		ipAddress.setText(s.getIpAddress());
		port.setText(s.getPort());
		mac_1.setText(s.getMacAddress().substring(0, 2));
		mac_2.setText(s.getMacAddress().substring(3, 5));
		mac_3.setText(s.getMacAddress().substring(6, 8));
		mac_4.setText(s.getMacAddress().substring(9, 11));
		mac_5.setText(s.getMacAddress().substring(12, 14));
		mac_6.setText(s.getMacAddress().substring(15, 17));
		
	}
	
	public void saveStringsToDatabase(boolean overwrite) {
		this.saveStringsToDatabase(-1,overwrite);
	}
	
	
	public void saveStringsToDatabase(int position, boolean overwrite) {
		
		ServerDatabase temp = new ServerDatabase();

		temp.setServerName(serverName.getText().toString());
		temp.setIpAddress(ipAddress.getText().toString());
		temp.setPort(port.getText().toString());

		if (position == -1) {
			
			for (int i = 0; i < listOfServers.size(); i ++) {
				if (listOfServers.get(i).getActive()) {
					lv.setItemChecked(i, true);
					storeDefault(i);
				}
			}
			
		} else {
			temp.setActive(listOfServers.get(position).getActive());			
		}
		
		String tempMac = mac_1.getText().toString() + ":" + mac_2.getText().toString() + 
				":" + mac_3.getText().toString() + ":" + mac_4.getText().toString() + 
				":" + mac_5.getText().toString() + ":" + mac_6.getText().toString();
		
		temp.setMacAddress(tempMac);
		
		if (!overwrite) {
			listOfServers.remove(position);
			listOfServers.add(position, temp);
		} else {
			listOfServers.add(temp);
		}	
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		int listSize = listOfServers.size();
		for (int i = 0; i < listSize; i++) {
			listOfServers.get(i).setActive(false);
		}
		listOfServers.get(position).setActive(true);
		lv.setItemChecked(position, true);
			
		storeDefault(position);
		store();		
	}

	private void storeDefault(int position) {
		
		ServerDatabase.setSelectedIp(listOfServers.get(position).getIpAddress());
		ServerDatabase.setSelectedPort(listOfServers.get(position).getPort());
		ServerDatabase.setSelectedMac(listOfServers.get(position).getMacAddress());	
		
		Log.d("adam","DATABASE IP : " + ServerDatabase.getSelectedIp());
		Log.d("adam","DATABASE Port : " + ServerDatabase.getSelectedPort());
		
		saveDefaultPrefs();
	}
	
	private void saveDefaultPrefs() {
		
		SharedPreferences appSharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this.getApplicationContext());
		Editor prefsEditor = appSharedPrefs.edit();

		String temp = String.valueOf(ServerDatabase.getSelectedPort()); 
		
		prefsEditor.putString("SelectedServerIp", ServerDatabase.getSelectedIp());
		prefsEditor.putString("SelectedServerPort", temp);
		prefsEditor.putString("SelectedServerMac", ServerDatabase.getSelectedMac());
		prefsEditor.commit();	
	}

}
