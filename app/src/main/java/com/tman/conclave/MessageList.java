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
            messages.add(new Messages("User 1","user1@gmail.com"));
            messages.add(new Messages("User 2","user2@gmail.com"));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
