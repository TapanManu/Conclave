package com.tman.conclave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    EditText message;
    ImageButton send, delete;
    ScrollView scrollView;
    String txt;
    RecyclerView chatArea;
    final String DEFAULT = "Your message here";
    final FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    public static ArrayList<Message> messages = new ArrayList<>();

    private void displayMessages(String id) {
        //display chat list
        recyclerView = findViewById(R.id.chatArea);
        recyclerView.setHasFixedSize(true);
        //recyclerView.scrollToPosition(messages.size()-1);
        // Log.d("size:",String.valueOf(users.size()));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);

        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        readMessages(this, id);

    }


    private void readMessages(Context context, String id) {

        final FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        Query recent = reference.limitToLast(100);

        recent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (messages != null) {
                    messages.clear();
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String key = snapshot.getKey();
                    String sender = snapshot.child("sender").getValue(String.class);
                    String receiver = snapshot.child("receiver").getValue(String.class);
                    String message = snapshot.child("message").getValue(String.class);
                    String time = snapshot.child("time").getValue(String.class);

                    Message m = new Message(key, sender, receiver, message, time);

                    if (m != null) {
                        if ((sender.equals(firebaseuser.getUid()) && receiver.equals(id))
                                || (sender.equals(id) && receiver.equals(firebaseuser.getUid()))) {
                            messages.add(m);
                        }
                    }


                }

                myAdapter = new MessageAdapter(context, messages);
                recyclerView.setAdapter(myAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("err", "error");
            }
        });

    }

    private void writeChatDatabase(String sender, String receiver, String message, String time) {

        assert firebaseuser != null;
        String userid = firebaseuser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        Log.d("entrychat", "entrychat");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("time", time);
        //push generates unique id for each chat
        reference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("success", "successfully added chats");
            }
        });

    }


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle extras = getIntent().getExtras();
        String name = extras.get("name").toString();
        String email = extras.get("email").toString();
        String id = extras.get("id").toString();
        String status = extras.get("status").toString();

        send = findViewById(R.id.Send);
        message = findViewById(R.id.message);
        message.setText(DEFAULT);
        delete = findViewById(R.id.delete);
        scrollView = findViewById(R.id.scrollView);
        chatArea = findViewById(R.id.chatArea);
        chatArea.setPadding(10, 5, 10, 5);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        View view = getSupportActionBar().getCustomView();
        TextView nameview = view.findViewById(R.id.nameView);
        TextView statusview = view.findViewById(R.id.status);
        CircleImageView profilepic = view.findViewById(R.id.image);
        nameview.setText(name);
        nameview.setTypeface(nameview.getTypeface(), Typeface.BOLD);
        statusview.setText(status);
        profilepic.setImageResource(R.drawable.profile);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Thread t = new Thread() {
            public void run() {
                displayMessages(id);
                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
            }
        };

        t.start();


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt = message.getText().toString().trim();
                Log.d("emsg", txt);
                if (txt.equals("") || txt.equals(DEFAULT)) {
                    Toast.makeText(getApplicationContext(), "Enter some text", Toast.LENGTH_SHORT).show();
                    message.setText(DEFAULT);
                } else {
                    Date value = Calendar.getInstance().getTime();
                    String time = value.toString().split(" ")[3].substring(0, 5);
                    writeChatDatabase(firebaseuser.getUid(), id, txt, time);
                    //create firebase database for storing chats
                    //populate the view by reading from database
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

    @Override
    protected void onResume() {
        super.onResume();
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}

