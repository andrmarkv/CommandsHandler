package com.example.andrey.commandshandler;

/**
 * Created by andrey on 07/05/17.
 */

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommandsHandlerService extends Service {
    private String tag = MainActivity.TAG;

    private final IBinder mBinder = new MyBinder();
    private List<String> resultList = new ArrayList<String>();
    private int counter = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d(tag, "CommandsHandlerService onStartCommand " + counter++);
        addResultValues();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
//        Log.d(tag, "CommandsHandlerService onBind" + counter++);
//        addResultValues();
        return mBinder;
    }

    public class MyBinder extends Binder {
        CommandsHandlerService getService() {
//            Log.d(tag, "CommandsHandlerService MyBinder.getService" + counter++);

            return CommandsHandlerService.this;
        }
    }

    public List<String> getWordList() {
//        Log.d(tag, "CommandsHandlerService getWordList" + counter++);

        return resultList;
    }

    private void addResultValues() {
//        Log.d(tag, "CommandsHandlerService addResultValues" + counter++);

        Random random = new Random();
        List<String> input = Arrays.asList("Linux", "Android","iPhone","Windows7" );
        resultList.add(input.get(random.nextInt(3)) + " " + counter++);
        if (counter == Integer.MAX_VALUE) {
            counter = 0;
        }
    }
}
