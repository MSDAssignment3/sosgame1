package com.example.sosgame1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;

public class ClientThread implements Runnable {

	private static final int SERVERPORT = 12345;
	private Handler handler = new Handler();
	Socket socket;
	private String serverIpAddress;
	private String temp = null;
	private DataInputStream in = null;
	private DataOutputStream out = null;
	private String uiMsg = null;
	private String recMsg = null;
	private String tempMsg = "";
	private int msgNum = -1;

	public ClientThread(String ip) {
		serverIpAddress = ip;
	}

	public void run() {
		try {
			Log.d("ClientActivity", "C: Connecting...");
			InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
			Socket socket = new Socket(serverAddr, SERVERPORT);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			int num = in.readInt();
			tempMsg = in.readUTF();
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					Log.d("MESSAGE RECEICED", tempMsg);
				}
			});
			while (true) {
				try {
					int messageType = 0;
					while(true)
					{
						messageType = in.readInt();
						switch (messageType) 
						{
						case Constant.MESSAGE:
							String mg = in.readUTF();
							Log.d("Client", mg);
							break;
						case Constant.EXIT:
							socket.close();
							break;
						}
					}

				} catch (Exception e) {
					Log.e("Client", "Error", e);
				}
			}
		} catch (Exception e) {
			Log.e("Client", "Error", e);
		}
	}

	public void setMessage(int msgtype, String message) {
		uiMsg = message;
		msgNum = msgtype;
		if (uiMsg != null && msgNum == 0) {
			Log.d("Client", "Sending command.");
			try {
				out.writeInt(msgNum);
				out.writeUTF(uiMsg);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d("Client", "Sent");
		}
	}

	public String getMessage() {
		return uiMsg;
	}

	public String getRecMsg() {
		return recMsg;
	}

	public void setRecMsg(String recMsg) {
		this.recMsg = recMsg;
	}

}