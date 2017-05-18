package com.example.andrey.commandshandler;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

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


    public ClientThread(String tag, String ipAddress, int port) {
        this.tag = tag;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void run() {

        try {
            InetAddress serverAddr = InetAddress.getByName(ipAddress);

            socket = new Socket(serverAddr, port);
            in = new BufferedInputStream(socket.getInputStream());
            out = new BufferedOutputStream(socket.getOutputStream());

            Log.i(tag, "Starting client thread");

            while (!Thread.currentThread().isInterrupted()) {

                try {
                    //Read length of the message
                    byte[] blen = new byte[4];
                    for (int i = 0; i < 4; i++) {
                        int b = in.read();

                        if (b < 0) {
                            Log.i(tag, "Exiting client thread");
                            break;
                        }
                    }
                    int len = Utils.byteArrayToInt(blen, 0);
                    if (len <= 0){
                        Log.e(tag, "Exiging, got wrong length: " + len);
                        break;
                    }

                    //Read body of the message
                    byte[] buf = new byte[len];

                    int msgId = Utils.byteArrayToInt(buf, 0);
                    int type = Utils.byteArrayToInt(buf, 4);

                    byte[] msg = Arrays.copyOf(buf, buf.length - 8);
                    handleMsg(type, msgId, buf);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            Log.i(tag, "Closing client thread");

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void handleMsg(int type, int msgId, byte[] buf) {
        Log.d(tag, "type: " + type + " msgId: " + msgId);
    }
}

