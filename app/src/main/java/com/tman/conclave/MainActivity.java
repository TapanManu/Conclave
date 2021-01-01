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

    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

    public static ArrayList<User> users;

    User currentuser;

    private void readUsers(Context context) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        //the users must be ordered according to recent message they have posted -> last time field

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
                    String imageURL = snapshot.child("imageURL").getValue(String.class);

                    User user = new User(key,username,email,imageURL);


                    if (user != null && user.getId() != null && firebaseUser != null && !user.getId().equals(firebaseUser.getUid())) {
                        Log.d("ui",user.getId());
                        users.add(user);
                    }
                    else if(user.getId().equals(firebaseUser.getUid())){
                        currentuser = user;
                    }
                }

                myAdapter = new UserAdapter(context, users);
                recyclerView.setAdapter(myAdapter);

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
        /*hashMap.put("search", username.toLowerCase());
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

    /*
    TODO :

    Message seen feature (add tick mark/change chat color/or any other UI feature)

    Chat message delete feature, forward feature

    Preloader Image for 1 second
    display while application loads data

    PROFILE SETTINGS OPTION SHOULD BE THERE (Tip:Add it to menu)
    ALONG WITH CHANGE IN USER NAME, PHOTO URL
    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName("set new display name")
                    .setPhotoUri(uri)
                    .build();
    FirebaseAuth.getInstance().getCurrentUser().updateProfile(userProfileChangeRequest);

    FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
    populate this to recycler view

    Add the snippet to that part for profile updation along with changes in firebase database fields

    File transfer feature


     */


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
            //display chat users
            Thread t = new Thread(() -> {
                changeStatus("online",fuser);
                displayChatusers();
            });

            t.start();
        }
    }

    private void displayChatusers(){
        //display message list
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);


        //linearLayoutManager.setSmoothScrollbarEnabled(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        readUsers((Context)this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("logged","logged in");
        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                fuser = FirebaseAuth.getInstance().getCurrentUser();
                writeToDataBase();
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                changeStatus("online",fuser);
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
        if(item.getItemId() == R.id.profile_menu) {
            Intent i = new Intent(MainActivity.this,ProfileActivity.class);
            MainActivity.this.startActivity(i);
        }
        if(item.getItemId() == R.id.menu_sign_out) {
            changeStatus("offline");
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();

                            // Close activity
                            finish();
                        }
                    });
        }
        return true;
    }



    @Override
    protected void onResume() {
        super.onResume();
        if(fuser!=null)
            changeStatus("online",fuser);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(fuser!=null)
            changeStatus("offline",fuser);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onItemClicked(int index) {

        User m = users.get(index);
        Intent intent = new Intent(MainActivity.this,ChatActivity.class);
        intent.putExtra("name",m.getName());
        intent.putExtra("id",m.getId());
        intent.putExtra("email",m.getEmail());
        intent.putExtra("status",m.getStatus());

        MainActivity.this.startActivity(intent);
        //put extras to intent based on the index selected
    }

    public void notifyDataChanged(){
        myAdapter.notifyDataSetChanged();
    }

    private void changeStatus(String status){
        if(currentuser!=null)
            currentuser.setStatus(status);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    private void changeStatus(String status,FirebaseUser fuser){
        if(currentuser!=null)
            currentuser.setStatus(status);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }




    }
