package com.tman.conclave;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {

    EditText message;
    ImageButton send,delete;
    ScrollView scrollView;
    String txt;
    LinearLayout chatArea;
    final String DEFAULT = "Your message here";
    int u=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        String name = extras.get("name").toString();

        send = findViewById(R.id.Send);
        message = findViewById(R.id.message);
        message.setText(DEFAULT);
        delete = findViewById(R.id.delete);
        scrollView = findViewById(R.id.scrollView);
        chatArea = findViewById(R.id.chatArea);
        chatArea.setPadding(10,5,10,5);

        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_action_name);
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
                    scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}