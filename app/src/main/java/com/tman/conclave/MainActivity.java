package com.tman.conclave;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText message;
    ImageButton send,delete;
    String txt;
    LinearLayout chatArea;
    final String DEFAULT = "Your message here";
    int u=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send = findViewById(R.id.Send);
        message = findViewById(R.id.message);
        message.setText(DEFAULT);
        delete = findViewById(R.id.delete);
        chatArea = findViewById(R.id.chatArea);
        chatArea.setPadding(10,5,10,5);

        getSupportActionBar().setTitle("Anonymous user");
        getSupportActionBar().setIcon(R.drawable.ic_launcher_foreground);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt = message.getText().toString().trim();
                Log.d("emsg",txt);
                if(txt.equals("") || txt.equals(DEFAULT)){
                    Toast.makeText(getApplicationContext(),"Enter some text",Toast.LENGTH_SHORT).show();
                    message.setText(DEFAULT);
                }
                else {
                    u = (u + 1)%2;
                    new MessageView(getApplicationContext(),u,txt,chatArea);
                    message.setText("");
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message.setText("");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        txt = message.getText().toString();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}