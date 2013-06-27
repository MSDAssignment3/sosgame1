package com.example.sosgame1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ClientThread implements Runnable {

	private static final int SERVERPORT = 12345;
	private Handler handler;// = new Handler();
	public Socket socket;
	private String serverIpAddress;
	private DataInputStream in = null;
	private DataOutputStream out = null;
	private String uiMsg = null;
	private String recMsg = null;
	private String tempMsg = "";
	private int msgNum = -1;
	public volatile boolean running = true;
	
	public ClientThread(String ip) {
		serverIpAddress = ip;
	}

	public ClientThread(String ip, Handler handler) {
		serverIpAddress = ip;
		this.handler = handler;
	}

	public void run() {
		while (running) {
			try {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Log.d("ClientActivity", "C: Connecting...");
					}
				});
				InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
				socket = new Socket(serverAddr, SERVERPORT);
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				int num = in.readInt();
				tempMsg = in.readUTF();
				handler.post(new Runnable() {
					@Override
					public void run() {
						Log.d("MESSAGE RECEIVED", tempMsg);
					}
				});
				while (running) {
					try {
						int messageType = 0;
						while(running) {
							try {
							messageType = in.readInt();
							} catch (final Exception e) {
								handler.post(new Runnable() {
									@Override
									public void run() {
										Log.d("Client", "Exception on readInt: " + e);
									}
								});
							}
							final int msgType = messageType;
							handler.post(new Runnable() {
								@Override
								public void run() {
									Log.d("Client", "message type: " + msgType);
								}
							});
							final String msg;
							Message msgz;
							Bundle b;
							switch (messageType) {
							case Constant.MESSAGE:
								try {
									final String mg = in.readUTF();
									handler.post(new Runnable() {
										@Override
										public void run() {
											Log.d("Client", mg);
										}
									});
								} catch (final Exception e) {
									handler.post(new Runnable() {
										@Override
										public void run() {
											Log.d("Client", "Exception on readUTF: " + e);
										}
									});
								}
								break;
							case Constant.SHOW_TILES_TO_CHOOSE:
							case Constant.CHOOSE_TILE:
								msg = in.readUTF();
								handler.post(new Runnable() {
									@Override
									public void run() {
										Log.d("Client", msg);
									}
								});
								msgz = new Message();
								msgz.arg1 = messageType;
								b = new Bundle();
								b.putString("PointF", msg);
								msgz.obj = b;
								handler.sendMessage(msgz);
								break;
							case Constant.BOARD_SIZE:
								msg = in.readUTF();
								handler.post(new Runnable() {
									@Override
									public void run() {
										Log.d("Client", msg);
									}
								});
								msgz = new Message();
								msgz.arg1 = messageType;
								b = new Bundle();
								b.putString("Size", msg);
								msgz.obj = b;
								handler.sendMessage(msgz);
								break;
							case Constant.EXIT:
								running = false;
								socket.shutdownInput();
								socket.shutdownOutput();
								socket.close();
								break;
							default:
								final String msg2 = in.readUTF();
								handler.post(new Runnable() {
									@Override
									public void run() {
										Log.d("Client message", msg2);
									}
								});
								break;	
							}
						}

					} catch (final Exception e) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								Log.e("Client", "Error", e);
							}
						});
					}
				}
			} catch (final Exception e) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Log.e("Client", "Error", e);
					}
				});
			}
		}
		if (socket != null) {
			try {
				socket.shutdownInput();
				socket.shutdownOutput();
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
				out.writeInt(msgNum);
				out.writeUTF(uiMsg);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			handler.post(new Runnable() {
				@Override
				public void run() {
					Log.d("Client", "Sent");
				}
			});
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