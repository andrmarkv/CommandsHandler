package com.example.andrey.commandshandler;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ScrollView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by andrey on 10/05/17.
 */

public class Client implements Runnable {
    private String tag;
    private HandlerContext context;

    private String ipAddress;
    private int port;

    private int msgId = 0;

    private Socket socket;
    private BufferedInputStream in;
    private BufferedOutputStream out;

    public static final int MESSAGE_ANDROID_TYPE_TEST = 1;
    public static final int MESSAGE_ANDROID_TYPE_TEST_EVENTS = 2;

    private boolean exit = false;

    private boolean isTestOK = true;


    public Client(HandlerContext context, String ipAddress, int port) {
        this.context = context;
        this.tag = context.getTag();
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public boolean isConnected(){
        boolean connected = true;

        connected = connected && in != null;
        connected = connected && out != null;

        return  connected;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void run() {
        Log.i(tag, "Starting client thread");

        try {
            InetAddress serverAddr = InetAddress.getByName(ipAddress);

            socket = new Socket(serverAddr, port);
            in = new BufferedInputStream(socket.getInputStream());
            out = new BufferedOutputStream(socket.getOutputStream());

            Log.i(tag, "Got connection to the server");

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
                        blen[i] = (byte)(b & 0xFF);
                    }

                    String tmp = Utils.hexdumptoString(blen, "", 0, 4);
                    Log.e(tag, "blen: " + tmp);

                    int len = (int) Utils.byteArrayToInt2(blen, 0);
                    Log.e(tag, "len: " + len);

                    if (len <= 0){
                        Log.e(tag, "Exiting, got wrong length: " + len);
                        break;
                    }

                    //Read body of the message
                    byte[] buf = new byte[len];
                    in.read(buf, 0, len);

                    int msgId = Utils.byteArrayToInt2(buf, 0);
                    int type = Utils.byteArrayToInt2(buf, 4);

                    byte[] msg = Arrays.copyOf(buf, buf.length - 8);
                    handleMsg(type, msgId, buf);

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
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

        Log.i(tag, "exiting run()");
    }

    private void handleMsg(int type, int msgId, byte[] buf) {
        switch (type){
            case MESSAGE_ANDROID_TYPE_TEST:
                if (msgId == this.msgId){
                    isTestOK = true;

                    context.addLogMessage("Test: " + msgId + " is OK");
                } else {
                    Log.e(tag, "Warning, got test message with wrong msgId. Expected: " + this.msgId + " got: " + msgId);
                }

                break;
            case MESSAGE_ANDROID_TYPE_TEST_EVENTS:
                context.addLogMessage("Got test events message: " + msgId );
                processTestEvetns(msgId);

                break;
            default:
                Log.e(tag, "Got unknown message");
        }
        Log.d(tag, "type: " + type + " msgId: " + msgId);
    }

    private void processTestEvetns(int msgId) {
        sendMsg(msgId, MESSAGE_ANDROID_TYPE_TEST_EVENTS, "OK".getBytes());

        try{
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            outputStream.writeBytes("input touchscreen swipe 300 300 500 1000 100\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();
        }catch(IOException e){
        }catch(InterruptedException e){
        }
    }

    public void sendMsg(int msgId, int type, byte[] msg){
        Log.d(tag, "Sending message, msgId: " + msgId);

        byte[] v1 = Utils.intToByteArray2(msgId);
        byte[] v2 = Utils.intToByteArray2(MESSAGE_ANDROID_TYPE_TEST);

        int len = v1.length + v2.length + msg.length + 4;
        byte[] v0 = Utils.intToByteArray2(len);

        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        try {
            bs.write(v0);
            bs.write(v1);
            bs.write(v2);
            bs.write(msg);
        } catch (Exception e){
            Log.e(tag, "Can't create message");
        }

        try {
            out.write(bs.toByteArray());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(tag, "Finished sending message, msgId: " + msgId);
    }

    public void sendTestMsg(){
        isTestOK = false; //We need to set status to false - only when correct confirmation will be obtained it should change to true

        msgId++; //Increment messageID

        Log.d(tag, "Sending test message, msgId: " + msgId);

        byte[] v1 = Utils.intToByteArray2(msgId);
        byte[] v2 = Utils.intToByteArray2(MESSAGE_ANDROID_TYPE_TEST);
        byte [] msg = new String ("This is test: " + msgId).getBytes();

        int len = v1.length + v2.length + msg.length + 4;
        byte[] v0 = Utils.intToByteArray2(len);

        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        try {
            bs.write(v0);
            bs.write(v1);
            bs.write(v2);
            bs.write(msg);
        } catch (Exception e){
            Log.e(tag, "Can't create test message");
        }

        try {
            out.write(bs.toByteArray());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(tag, "Finished sending test message, msgId: " + msgId);
    }

    public void close(){
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (Exception e){

        }
    }
}

