package com.nikhil.bluetoothchat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ChatActivity extends Activity {

	
	ObjectOutputStream out;
	ObjectInputStream in;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		String type;
		Intent myIntent = this.getIntent();
		type=myIntent.getExtras().getString("sendBy");
		if(type.equals("server")){
			out = BluetoothServerConnection.out;
			in = BluetoothServerConnection.in;
		}
		else if(type.equals("client")){
			out = BluetoothClientConnection.out;
			in = BluetoothClientConnection.in;
		}
		RecieveData rd = new RecieveData();
		rd.execute();
	}
	
	public void selectedSend(View v){
		EditText msg = (EditText) findViewById(R.id.message);
		String message = msg.getText().toString();
		TextView chat = (TextView) findViewById(R.id.chat);
		chat.append("\nMe>> "+message);
		try {
			out.write(message.getBytes());
		} catch (IOException e) {e.printStackTrace();}
	}
	
	private class RecieveData extends AsyncTask<Void,Void,Void>{
		String msg;
		@Override
		protected Void doInBackground(Void... params) {
			while(in!=null){
				try {
					msg = (String)in.readObject();
					ChatActivity.this.runOnUiThread(new Runnable(){

						@Override
						public void run() {
							TextView chat = (TextView) findViewById(R.id.chat);
							chat.append("\nOther>> "+msg);
						}
						
					});
				} catch (Exception e) {e.printStackTrace();} 
			}
			return null;
		}
		
	}
	
}
