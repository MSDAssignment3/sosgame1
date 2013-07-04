package com.example.sosgame1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Server implements Runnable{
	
	//Default IP Address
    public static String SERVERIP = Utils.getIPAddress(true);
    private Handler handler;
    public Socket socket;
    public ServerSocket serverSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private String message = "";

    private int boardRows = 0;
    private int boardColumns = 0;
    
    public volatile boolean running = true;

    private String remoteIPAddress;
    
    public Server(Handler handler) {
    	this.handler = handler;
    }
    
    public void setBoard(int boardRows, int boardColumns) {
    	this.boardRows = boardRows;
    	this.boardColumns = boardColumns;
    }
    
    public boolean connected = false;
    
	@Override
	public void run() {
		while (running) {
			if (SERVERIP != null) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Log.d("SERVER", "Listening on IP: " + SERVERIP);
					}
				});
				try {
					serverSocket = new ServerSocket(Constant.SERVER_PORT);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while (running) {
					// Must set a timeout otherwise serverSocket.accept()
					// will block infinitely and the thread cannot be ended.
					int timeout = 3000;
					try {
						serverSocket.setSoTimeout(timeout);
					} catch (SocketException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					// listen for incoming clients
					try {
						socket = serverSocket.accept();
						connected = true;
						remoteIPAddress = socket.getRemoteSocketAddress().toString();
						in = new DataInputStream(socket.getInputStream());
						out = new DataOutputStream(socket.getOutputStream());
					} catch (final Exception e1) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								Log.e("SERVER", "Exception", e1);
							}
						});
					}
					
					if (connected) {
						setMessage(Constant.MESSAGE, "You are connected");
						handler.post(new Runnable() {
							@Override
							public void run() {
								Log.d("SERVER","Connected.");
							}
						});

						Message message = new Message();
						message.arg1 = Constant.SERVER_CONNECTED;
						handler.sendMessage(message);

						// Wait here in case the client connected before
						// we have the board size.
						while ((boardRows == 0 || boardColumns == 0) && running) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						setMessage(Constant.BOARD_SIZE, boardRows + "," + boardColumns);
					}
					
					while (running && connected) {
						try {
							int messageType = in.readInt();
							getAndProcessMessage(messageType);
						} catch (final Exception e) {
							connected = false;
							handler.post(new Runnable() {
								@Override
								public void run() {
									Log.e("Server", "exception:", e);
//									Log.d("Server","Oops. Connection interrupted. Please reconnect your phones.");
								}
							});
							e.printStackTrace();
						}
					}
				}
			} else {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Log.d("Server","Couldn't detect internet connection.");
					}
				});
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }

	private void getAndProcessMessage(int messageType) throws IOException {
		String msg = "";
		try {
			msg = in.readUTF();
		} catch (final Exception e) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					Log.d("Server", "Exception on readUTF: " + e);
				}
			});
		}
		final String msgF = msg;
		switch(messageType) {
		case Constant.MESSAGE:
			handler.post(new Runnable() {
				@Override
				public void run() {
					Log.d("Server", msgF);
				}
			});
			setMessage(Constant.MESSAGE, "Message received: " + message);
			break;
		case Constant.SHOW_TILES_TO_CHOOSE:
		case Constant.CHOOSE_TILE:
		case Constant.QUERY_BOARD_SIZE:
			handler.post(new Runnable() {
				@Override
				public void run() {
					Log.d("Server", msgF);
				}
			});
			Message message = new Message();
			message.arg1 = messageType;
			Bundle bundle = new Bundle();
			bundle.putString("Message", msg);
			message.obj = bundle;
			handler.sendMessage(message);
			break;
		case Constant.EXIT:
			running = false;
			setMessage(Constant.EXIT, "Exit");
			socket.close();
			break;
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
				if (!socket.isOutputShutdown()) {
					out.writeInt(msgType);
					out.writeUTF(message);
					out.flush();
					handler.post(new Runnable() {
						@Override
						public void run() {
							Log.d("Server", "Sent To Client");
						}
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
