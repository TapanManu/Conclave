package com.tman.conclave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements UserAdapter.ItemClicked {
    private static final int SIGN_IN_REQUEST_CODE = 10 ;
    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    View customView;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = mDatabase.getReference().child("user");

    public static ArrayList<User> users;

    private void readUsers(Context context) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(users!=null)
                    users.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //User user = snapshot.getValue(User.class);
                    String key = snapshot.getKey();
                    String username = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    User user = new User(key,username,email);


                    if (user != null && user.getId() != null && firebaseUser != null && !user.getId().equals(firebaseUser.getUid())) {
                        Log.d("ui",user.getId());
                        users.add(user);
                    }
                }

                myAdapter = new UserAdapter(context, users);
                recyclerView.setAdapter(myAdapter);

                /*if (users.size() == 0) {
                    frameLayout.setVisibility(View.VISIBLE);
                } else {
                    frameLayout.setVisibility(View.GONE);
                }*/




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("err","error");
            }
        });

    }

    public void writeToDataBase(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        assert firebaseUser != null;
        String userid = firebaseUser.getUid();
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        Log.d("entry","entry");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", userid);
        hashMap.put("username",firebaseUser.getDisplayName() );
        hashMap.put("imageURL", "default");
        hashMap.put("status", "offline");
        hashMap.put("email",firebaseUser.getEmail());
        /*hashMap.put("bio", "");
        hashMap.put("search", username.toLowerCase());
        if(dialog!=null){
            dialog.dismiss();
        }*/
        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("success","successfully added user");
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        users = new ArrayList<User>();

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        }
        else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();
            //display chat users

               //users.add(new User("1", "dummy", "dummy@gmail.com"));


            displayChatusers();
        }
    }

    private void displayChatusers(){
        //display message list
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

       // Log.d("size:",String.valueOf(users.size()));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        readUsers((Context)this);
        //Log.d("BAG",String.valueOf(users.size()));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("logged","logged in");
        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                writeToDataBase();
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                changeStatus("online");
                displayChatusers();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();
                            changeStatus("offline");
                            // Close activity
                            finish();
                        }
                    });
        }
        return true;
    }



    @Override
    public void onItemClicked(int index) {

        User m = users.get(index);
        Intent intent = new Intent(MainActivity.this,ChatActivity.class);
        intent.putExtra("name",m.getName());


        //get the name and email id of other user to display the users and scrutinize based on that
        //create two variables current user identified by firebase.AuthUI.getcurrentuser()
        //other is other user identified by this
        //call message view constructor by passing this info to display list of prev users
        //better to call a somewhat modified function to display chat
        //so it clearly implies each message should have a field representing which user has send that message
        //then only this would be possible
        //so start with users, make some modification necessarily
        //create a class/field(preferred) if needed to represent the scenario
        //each chats typed by the current user must be stored into firebase realtime database along with content,
        //timestamp,email or userID
        //scrutinize this as personal or public/other person's chat using this technology


        MainActivity.this.startActivity(intent);
        //put extras to intent based on the index selected
    }

    public void notifyDataChanged(){
        myAdapter.notifyDataSetChanged();
    }

    private void changeStatus(String status){
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }


    }
