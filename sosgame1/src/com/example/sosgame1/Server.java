package com.example.sosgame1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Server implements Runnable{
	
   //Default IP Address
 
    // default ip
    public static String SERVERIP = Utils.getIPAddress(true);
    String line = null;
    private Handler handler;// = new Handler();
    public Socket client;
    public ServerSocket serverSocket;
    private String temp = "";
    private DataInputStream in;
    private DataOutputStream out;
    private String message = "";

    public volatile boolean running = true;

    private int boardRows;
    private int boardColumns;
    
    public Server(Handler handler) {
    	this.handler = handler;
    }
    
    public void setBoard(int rows, int columns) {
    	boardRows = rows;
    	boardColumns = columns;
    }
    
	@Override
	public void run() {
		while (running) {
			try {
				if (SERVERIP != null) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							Log.d("SERVER", "Listening on IP: " + SERVERIP);
						}
					});
					serverSocket = new ServerSocket(Constant.SERVER_PORT);
					while (running) {
						// listen for incoming clients
						client = serverSocket.accept();
						in = new DataInputStream(client.getInputStream());
						out = new DataOutputStream(client.getOutputStream());
						out.writeInt(Constant.MESSAGE);
						out.writeUTF("You are connected");
						handler.post(new Runnable() {
							@Override
							public void run() {
								Log.d("SERVER","Connected.");
							}
						});

						// Wait here in case the client started and connected 
						// before we have the board size.
						while (boardRows == 0 || boardColumns == 0) {
							Thread.sleep(20);
						}
						// Tell the connected client what the board size is.
		            	setMessage(Constant.BOARD_SIZE, boardRows + "," 
		            							+ boardColumns);

						try {
							while(running){
								int messageType = in.readInt();
								switch(messageType)
								{
								case Constant.MESSAGE:
									temp = in.readUTF();
									handler.post(new Runnable() {

										@Override
										public void run() {
											Log.d("Server",temp);

										}
									});
									out.writeInt(Constant.MESSAGE);
									out.writeUTF("Message recieved: "+ temp);
									out.flush();
									break;
								case Constant.SHOW_TILES_TO_CHOOSE:
								case Constant.CHOOSE_TILE:
									final String msg = in.readUTF();
									handler.post(new Runnable() {
										@Override
										public void run() {
											Log.d("Client", msg);
										}
									});
									Message msgz = new Message();
									msgz.arg1 = messageType;
									Bundle b = new Bundle();
									b.putString("PointF", msg);
									msgz.obj = b;
									handler.sendMessage(msgz);
									break;
								case Constant.EXIT:
									running = false;
									client.shutdownInput();
									client.shutdownOutput();
									client.close();
									break;
								}
							}
						} catch (Exception e) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									Log.d("Server","Oops. Connection interrupted. Please reconnect your phones.");
								}
							});
							e.printStackTrace();
						}
					}
				} else {
					handler.post(new Runnable() {
						@Override
						public void run() {
							Log.d("Server","Couldn't detect internet connection.");
						}
					});
				}
			} catch (Exception e) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Log.d("Server","Error");
					}
				});
				e.printStackTrace();
			}
		}
		if (serverSocket != null) {
			try {
				client.shutdownInput();
				client.shutdownOutput();
				client.close();
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }

	public String getMessage()
	{
		return message;
	}  
	
	public void setMessage(int msgType, String msgIn)
	{
		String message = msgIn;
		if (message != null && out != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                	Log.d("Server", "Sending message in progress");
                }
            });
			try {
				out.writeInt(msgType);
				out.writeUTF(message);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
            handler.post(new Runnable() {
                @Override
                public void run() {
                	Log.d("Server", "Sent To Client");
                }
            });
		}
	}
}
