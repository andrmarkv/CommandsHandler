package com.example.andrey.commandshandler;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by andrey on 10/05/17.
 */

public class ClientThread implements Runnable {
    private String tag;

    private String ipAddress;
    private int port;

    private Socket socket;
    private BufferedInputStream in;
    private BufferedOutputStream out;



    public ClientThread(String tag, String ipAddress, int port){
        this.tag = tag;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public void run() {

        try {
            InetAddress serverAddr = InetAddress.getByName(ipAddress);

            socket = new Socket(serverAddr, port);
            in = new BufferedInputStream(socket.getInputStream());
            out = new BufferedOutputStream(socket.getOutputStream());

            while (!Thread.currentThread().isInterrupted()) {

                try {
                    byte[] blen = new byte[4];
                    for (int i = 0; i < 4; i++) {
                        int b = in.read();

                        if (b < 0) {
                            Log.i(tag, "Exiting client thread");
                            break;
                        }
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}

