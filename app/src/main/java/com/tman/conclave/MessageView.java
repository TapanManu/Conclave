package com.tman.conclave;

import android.content.Context;
import android.graphics.Color;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tman.conclave.R;

public class MessageView {

    TextView sendmessage,recievemessage;



    MessageView(Context context, int user,String message,LinearLayout chatArea){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if(user==1) {
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
        else if(user==0){
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
    }



}


