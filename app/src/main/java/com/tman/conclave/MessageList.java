package com.tman.conclave;

import android.app.Application;

import java.util.ArrayList;

public class MessageList extends Application {
    public static ArrayList<Messages> messages;

    @Override
    public void onCreate() {
        super.onCreate();
        messages = new ArrayList<Messages>();
        try {
            messages.add(new Messages("User 1"));
            messages.add(new Messages("User 2"));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
