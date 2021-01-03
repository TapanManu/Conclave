package com.tman.conclave;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 1;

    Uri selectedImage;
    StorageReference storageReference;

    TextView UserName,disp,submitname,cancel;
    EditText etuser;
    CircleImageView cprof;
    ImageButton editImage,editusername;
    BottomSheetDialog dialog;
    LinearLayout dialog_box ;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference;

    StorageTask<UploadTask.TaskSnapshot> uploadTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        storageReference = FirebaseStorage.getInstance().getReference("uploads");


        Bundle extras = getIntent().getExtras();
        String image = extras.get("image").toString();

        //Use glide to load images from web
        //from phone, start an implicit activity
        //https://stackoverflow.com/questions/62530852/how-to-display-pictures-from-phone-in-cards-in-android-studio-like-whatsapp-doe

        UserName = findViewById(R.id.username);
        disp = findViewById(R.id.disp);
        cprof = findViewById(R.id.userprof);
        editImage = findViewById(R.id.editImage);

        editusername = findViewById(R.id.editUserName);

        disp.setText(firebaseUser.getDisplayName());
        if(image.equals("default")){
            cprof.setImageResource(R.drawable.profile);
        }
        else {
            Log.d("TAG",firebaseUser.getPhotoUrl().toString());
            Glide.with(getApplicationContext())
                    .load(image)
                    .into(cprof);
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

        editImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });


    }

    // UploadImage method
    private void uploadImage()
    {
        if (selectedImage != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            uploadTask = ref.putFile(selectedImage);
            uploadTask
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("imageURL", ""+selectedImage);
                                    reference.updateChildren(map);
                                    Glide.with(getApplicationContext())
                                            .load(selectedImage)
                                            .into(cprof);
                                    progressDialog.dismiss();
                                    Toast.makeText(ProfileActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(ProfileActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
        else{
            Toast.makeText(ProfileActivity.this,"no image chosen",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            selectedImage = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
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