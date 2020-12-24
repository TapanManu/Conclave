package com.tman.conclave;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.tman.conclave.R;

import java.util.Calendar;
import java.util.Date;

public class MessageView {

    TextView sendmessage,recievemessage,datetime;



    MessageView(Context context, String username,String message,LinearLayout chatArea){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        String currentuser= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        if(username.equals(currentuser)) {               //current user
            Log.d("success","hi");
            sendmessage = new TextView(context);
            sendmessage.setPadding(30,10,30,10);
            sendmessage.setText(message);
            sendmessage.setTextColor(Color.WHITE);
            sendmessage.setLayoutParams(lp);
            lp.gravity = Gravity.RIGHT;
            lp.setMargins(50,10,50,0);
            sendmessage.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            sendmessage.setBackgroundResource(R.drawable.user_message);
            chatArea.addView(sendmessage);
        }
        else{       //anonymous user
            recievemessage = new TextView(context);
            recievemessage.setText(message);
            recievemessage.setPadding(30,10,30,10);
            recievemessage.setTextColor(Color.BLACK);
            recievemessage.setLayoutParams(lp);
            lp.setMargins(50,10, 50,0);
            recievemessage.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            recievemessage.setBackgroundResource(R.drawable.ext_message);
            chatArea.addView(recievemessage);
        }

        //time display
        Date time = Calendar.getInstance().getTime();
        String times = time.toString().split(" ")[3];
        datetime = new TextView(context);
        datetime.setText(times);
        datetime.setTextColor(Color.WHITE);
        datetime.setLayoutParams(lp);
        datetime.setPadding(30,10,30,10);
        datetime.setBackgroundColor(Color.parseColor("#cccccc"));

        chatArea.addView(datetime);

    }





}


