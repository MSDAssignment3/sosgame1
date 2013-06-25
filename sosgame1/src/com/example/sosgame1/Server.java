package com.example.sosgame1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;

public class Server implements Runnable{
	
   //Default IP Address
 
    // default ip
    public static String SERVERIP = Utils.getIPAddress(true);
    String line = null;
    private Handler handler = new Handler();
    private Socket client;
    private ServerSocket serverSocket;
    private String temp = "";
    private DataInputStream in;
    private DataOutputStream out;
    private String message = "";
	@Override
	public void run() {
        try {
            if (SERVERIP != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("SERVER", "Listening on IP: " + SERVERIP);
                    }
                });
                serverSocket = new ServerSocket(Constant.SERVER_PORT);
                while (true) {
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

                    try {
                      while(true){
                      	int msg = in.readInt();
                      	switch(msg)
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
                      	case Constant.POINT:
                      		temp = in.readUTF();
                      		Log.d("Server",temp);
                      	case Constant.EXIT:
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

	public String getMessage()
	{
		return message;
	}  
	
	public void setMessage(int msgType, String msgIn)
	{
		if (message != null && out != null) {
			Log.d("Server", "Sending message in progress");
			try {
				out.writeInt(msgType);
				out.writeUTF(message);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d("Server", "Sent To Client");
		}
	}
}
