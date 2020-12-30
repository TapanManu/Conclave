package com.tman.conclave;

import android.app.Application;

public class Conclave extends Application {
    public static String lastchat;
    public static String lasttime;

    @Override
    public void onCreate() {
        super.onCreate();
        lastchat = "no chat done";
        lasttime = null;
    }
}
