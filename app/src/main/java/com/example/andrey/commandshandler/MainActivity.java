package com.example.andrey.commandshandler;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements ServiceConnection {
    public String TAG = getString(R.string.TAG);
    private CommandsHandlerService service;

    private TextView slog;
    private ScrollView sview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slog = (TextView)findViewById(R.id.serviceLog);
        sview = (ScrollView) findViewById(R.id.textAreaScroller);
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
        if (service != null) {
        }

        slog.append("Test Start\n");
        sview.fullScroll(ScrollView.FOCUS_DOWN);
    }

    public void onClickStop(View view) {
        if (service != null) {
        }

        slog.append("Test Stop\n");
        sview.fullScroll(ScrollView.FOCUS_DOWN);
    }

    public void onClickStatus(View view) {
        if (service != null) {
        }
        slog.append("Test Status\n");
        sview.fullScroll(ScrollView.FOCUS_DOWN);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        CommandsHandlerService.MyBinder b = (CommandsHandlerService.MyBinder) binder;
        service = b.getService();
        slog.append("Service Connected\n");
        sview.fullScroll(ScrollView.FOCUS_DOWN);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
        slog.append("Service Disconnected\n");
        sview.fullScroll(ScrollView.FOCUS_DOWN);
    }


}
