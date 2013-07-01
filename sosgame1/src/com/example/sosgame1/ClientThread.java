package com.example.sosgame1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ClientThread implements Runnable {

	private Handler handler;
	public Socket socket;
	private String serverIpAddress;
	private DataInputStream in = null;
	private DataOutputStream out = null;
	private String uiMsg = null;
	private String recMsg = null;
	private int msgNum = -1;
	public volatile boolean running = true;
	private MainActivity activity;
	
	public ClientThread(String ip) {
		serverIpAddress = ip;
	}

	public ClientThread(String ip, Handler handler, MainActivity activity) {
		serverIpAddress = ip;
		this.handler = handler;
		this.activity = activity;
	}

	public void run() {
		Message message;
		int connectionAttempts;
		boolean connected = false;
		while (running) {
			connectionAttempts = 0;
			
			while (!connected && running) {
				connectionAttempts++;
				final int attempts = connectionAttempts;
				InetAddress serverAddr = null;
				try {
					serverAddr = InetAddress.getByName(serverIpAddress);
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						Log.d("Client", "Connecting...");
						activity.alertDialog.setMessage("Connecting to " + serverIpAddress
								+ ".\nAttempt " + attempts);
					}
				});
				try {
					socket = new Socket(serverAddr, Constant.SERVER_PORT);
					connected = true;
					// Send message to the UI thread to dismiss the connecting dialog
					message = new Message();
					message.arg1 = Constant.CLIENT_CONNECTED;
					handler.sendMessage(message);
					handler.post(new Runnable() {
						@Override
						public void run() {
							if (socket.isConnected()) {
								Log.d("Client", "socket connected");
							}
						}
					});
				} catch (final IOException e) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							Log.e("Client", "Connection error", e);
						}
					});
				}
				if (!connected) {
					// Sleep for a time so the UI thread can cancel.
					try {
						Thread.sleep(1000);
					} catch (final InterruptedException e1) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								Log.e("Client", "Interrupted exception:", e1);
							}
						});
						e1.printStackTrace();
					}
				}
			}
			
			if (connected) {
				try {
					in = new DataInputStream(socket.getInputStream());
					out = new DataOutputStream(socket.getOutputStream());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
//				setMessage(Constant.QUERY_BOARD_SIZE, "Message");
				while (running && connected) {
					int messageType = 0;
					if (socket.isConnected()) {
						try {
							messageType = in.readInt();
							final int msgType = messageType;
							handler.post(new Runnable() {
								@Override
								public void run() {
									Log.d("Client", "message type: " + msgType);
								}
							});
							getAndProcessMessage(messageType);
						} catch (final Exception e) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									Log.d("Client", "Exception on readInt: " + e);
								}
							});
							// Sleep for a time so the UI thread can cancel.
							try {
								Thread.sleep(500);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							connected = false;
						}
					} else {
						connected = false;
					}
				}
			}
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void getAndProcessMessage(int messageType) {
		Message message;
		Bundle bundle;
		String msg = "";
		try {
			msg = in.readUTF();
		} catch (final Exception e) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					Log.d("Client", "Exception on readUTF: " + e);
				}
			});
		}
		switch (messageType) {
		case Constant.MESSAGE:
		case Constant.SHOW_TILES_TO_CHOOSE:
		case Constant.CHOOSE_TILE:
		case Constant.BOARD_SIZE:
			final String msgF = msg;
			handler.post(new Runnable() {
				@Override
				public void run() {
					Log.d("Client", msgF);
				}
			});
			message = new Message();
			bundle = new Bundle();
			message.arg1 = messageType;
			bundle.putString("Message", msg);
			message.obj = bundle;
			handler.sendMessage(message);
			break;
		case Constant.EXIT:
			running = false;
			setMessage(Constant.EXIT, "Exit");
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}

	public void setMessage(int msgtype, String message) {
		uiMsg = message;
		msgNum = msgtype;
		if (uiMsg != null && out != null) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					Log.d("Client", "Sending command.");
				}
			});
			try {
				if (!socket.isOutputShutdown()) {
					out.writeInt(msgNum);
					out.writeUTF(uiMsg);
					out.flush();
					handler.post(new Runnable() {
						@Override
						public void run() {
							Log.d("Client", "Sent");
						}
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
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