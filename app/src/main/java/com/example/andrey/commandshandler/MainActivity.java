package com.example.andrey.commandshandler;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity implements ServiceConnection {
    public static String TAG = "CommandsHandler";
    private CommandsHandlerService service;

    private TextView slog;
    private ScrollView sview;
    private HandlerContext context;

    private String serverAddress;
    private int serverPort;
    private Client client;
    private Thread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity start");
        setContentView(R.layout.activity_main);

        slog = (TextView)findViewById(R.id.serviceLog);
        sview = (ScrollView) findViewById(R.id.textAreaScroller);

        context = new HandlerContext(this, TAG, slog, sview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent= new Intent(this, CommandsHandlerService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    public void onClickStart(View view) {
        Log.d(TAG, "Activity onClickStart");

        if (client != null && client.isConnected()){
            Log.d(TAG, "Client is running, not creating");
        } else {
            Log.d(TAG, "Creating new client");
            serverAddress = ((EditText)findViewById(R.id.ipAddr)).getText().toString();
            String tmp = ((EditText)findViewById(R.id.port)).getText().toString();
            if (tmp != null && tmp.length() > 0){
                serverPort = Integer.parseInt(tmp);
            } else{
                Log.e(TAG, "Can't parse port value");
            }

            client = new Client(context, serverAddress, serverPort);
            clientThread = new Thread(client);
            clientThread.start();

            context.addLogMessage("New client was started");
        }
    }

    public void onClickStop(View view) {
        Log.d(TAG, "Closing client...");

        if (client != null) {
            client.close();
        }

        client = null;

        context.addLogMessage("Closed client");
    }

    public void onClickStatus(View view) {
        context.addLogMessage("Initiating Test...");

        if (client != null && client.isConnected()) {
            client.sendTestMsg();
        } else {
            context.addLogMessage("Client is not connected!");
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        CommandsHandlerService.MyBinder b = (CommandsHandlerService.MyBinder) binder;
        service = b.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }


}
