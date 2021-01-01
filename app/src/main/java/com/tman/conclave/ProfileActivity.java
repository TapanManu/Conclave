package com.tman.conclave;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    TextView UserName,disp;
    EditText etuser;
    CircleImageView cprof;
    ImageButton editImage,editusername;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        UserName = findViewById(R.id.username);
        disp = findViewById(R.id.disp);
        cprof = findViewById(R.id.userprof);
        editImage = findViewById(R.id.editImage);
        editusername = findViewById(R.id.editUserName);
        disp.setText(firebaseUser.getDisplayName());
        if(firebaseUser.getPhotoUrl() == null){
            cprof.setImageResource(R.drawable.profile);
        }
        else {
            cprof.setImageURI(firebaseUser.getPhotoUrl());
        }
        editusername.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}