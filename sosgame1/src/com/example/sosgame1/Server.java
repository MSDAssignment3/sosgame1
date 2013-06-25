package com.example.sosgame1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.os.Handler;
import android.util.Log;

public class Server implements Runnable{
	
    public static String SERVERIP = "10.0.2.15"; //Default IP Address
 
    public static final int SERVERPORT = 12345;
 
    private Handler handler = new Handler();
    private String message;
    private ServerSocket serverSocket;
	@Override
	public void run() {
		try {
            if (SERVERIP != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        message = "Listening on IP: " + SERVERIP;
                    }
                });
                serverSocket = new ServerSocket(SERVERPORT);
                while (true) {
                    Socket client = serverSocket.accept();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                        	message = "Connected.";
                        }
                    });

                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        String line = null;
                        while ((line = in.readLine()) != null) {
                            Log.d("ServerActivity", line);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // do whatever you want to the front end
                                    // this is where you can be creative
                                }
                            });
                        }
                        break;
                    } catch (Exception e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                            	message = "Oops. Connection interrupted. Please reconnect your phones.";
                            }
                        });
                        e.printStackTrace();
                    }
                }
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                    	message = "Couldn't detect internet connection.";
                    }
                });
            }
        } catch (Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                	message = "Error";
                }
            });
            e.printStackTrace();
        }
	}

	public String displayMessage()
	{
		return message;
	}
	
	//Retrieves the Local Ip Address
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    { 
                    	return inetAddress.getHostAddress().toString(); 
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("ServerActivity", ex.toString());
        }
        return null;
    }
   
}
