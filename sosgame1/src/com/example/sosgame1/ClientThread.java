package com.example.sosgame1;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;

public class ClientThread implements Runnable {
	 
    private int port = Constant.SERVER_PORT;
    Socket socket;
    PrintWriter out;
    String serverIpAddress;
    public ClientThread(String ip)
    {
    	serverIpAddress = ip;
    }
	public void run() {
        boolean connected;
		try {
            InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
            Log.d("ClientActivity", "C: Connecting..."+ serverAddr);
            socket = new Socket(serverAddr, port);
            connected = true;
            while (connected) {
                try {
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                                .getOutputStream())), true);
                        
                        out.println("Signal Server!");
                } catch (Exception e) {
                    Log.e("ClientActivity", "S: Error", e);
                }
            }
            socket.close();
            Log.d("ClientActivity", "C: Closed.");
        } catch (Exception e) {
            Log.e("ClientActivity", "C: Error", e);
            connected = false;
        }
    }
}
