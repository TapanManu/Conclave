package com.tman.conclave;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    TextView UserName,disp,submitname,cancel;
    EditText etuser;
    CircleImageView cprof;
    ImageButton editImage,editusername;
    BottomSheetDialog dialog;
    LinearLayout dialog_box ;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Use glide to load images from web
        //from phone, start an implicit activity
        //https://stackoverflow.com/questions/62530852/how-to-display-pictures-from-phone-in-cards-in-android-studio-like-whatsapp-doe

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
                dialog = new BottomSheetDialog(ProfileActivity.this);
                dialog.setContentView(R.layout.change_name);
                submitname = dialog.findViewById(R.id.Submit_name);
                cancel = dialog.findViewById(R.id.cancel);
                etuser = dialog.findViewById(R.id.etName);
                dialog.show();
                edit_Name();



            }
        });


    }
    private void  edit_Name(){
        submitname.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String mName = etuser.getText().toString();
                disp.setText(mName);
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(mName).build();

                firebaseUser.updateProfile(profileUpdates);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("username",mName);

                reference.updateChildren(hashMap);
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
    }
}