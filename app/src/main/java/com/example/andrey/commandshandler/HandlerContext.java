package com.example.andrey.commandshandler;

import android.app.Activity;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by andrey on 5/20/17.
 */

public class HandlerContext {
    private String tag;
    private TextView slog;
    private ScrollView sview;
    private Activity activity;

    public HandlerContext(Activity parentActivity, String tag, TextView slog, ScrollView sview) {
        this.activity = parentActivity;
        this.tag = tag;
        this.slog = slog;
        this.sview = sview;
    }

    public String getTag() {
        return tag;
    }

    public void addLogMessage(final String msg){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                slog.append(msg + "\n");
                sview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });


    }
}
