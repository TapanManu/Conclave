package com.tman.conclave;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<User> users;
    ItemClicked activity;
    Context context;
    String LastMessage,Lasttime;

    public interface ItemClicked{
        void onItemClicked(int index);
    }

    public UserAdapter(Context appcontext,Context context, ArrayList<User> list){
        this.users = list;
        if(users==null){
            Log.d("error","hi");
        }
        this.activity = (ItemClicked) context;
        this.context = appcontext;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messagelayout,viewGroup,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(users.get(position));
        Log.d("namesview", users.get(position).getImageURL().toString());
        holder.nameview.setText(users.get(position).getName());
        if (users.get(position).getImageURL().toString().equals("default")) {
            Log.d("checkI","checking");
            holder.prof.setImageResource(R.drawable.profile);
    }
        else {
            Log.d("checkII",users.get(position).getImageURL().toString());
            Glide.with(holder.itemView.getContext())
                    .load(users.get(position).getImageURL().toString())
                    .into(holder.prof);
            //holder.prof.setImageURI(users.get(position).getImageURL());
        }
         //   holder.prof.setImageResource(R.drawable.profile);
            String userid = users.get(position).getId();
            lastMessage(userid, holder.chat, holder.time);

    }

        @Override
        public int getItemCount () {
            return users.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView nameview;
            CircleImageView prof;
            TextView chat;
            TextView time;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                nameview = itemView.findViewById(R.id.tvName);
                prof = itemView.findViewById(R.id.ivProf);
                chat = itemView.findViewById(R.id.lastchat);
                time = itemView.findViewById(R.id.time);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.onItemClicked(users.indexOf(view.getTag()));
                    }
                });
            }
        }

        private void lastMessage ( final String userid, final TextView last_msg,
        final TextView last_time){
            LastMessage = "default";
            Lasttime = "";
            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String sender = snapshot.child("sender").getValue(String.class);
                        String receiver = snapshot.child("receiver").getValue(String.class);
                        String message = snapshot.child("message").getValue(String.class);
                        String time = snapshot.child("time").getValue(String.class);

                        if (firebaseUser != null && message != null) {
                            if (receiver.equals(firebaseUser.getUid()) && sender.equals(userid) ||
                                    receiver.equals(userid) && sender.equals(firebaseUser.getUid())) {
                                LastMessage = message;
                                Lasttime = time;
                            }
                        }
                    }

                    switch (LastMessage) {
                        case "default":
                            last_msg.setText("No Message");
                            last_time.setText("");
                            break;

                        default:
                            if (LastMessage.length() > 30) {
                                last_msg.setText(LastMessage.substring(0, 30) + "...");
                            } else {
                                last_msg.setText(LastMessage);
                            }
                            last_time.setText(Lasttime);
                            break;
                    }

                    LastMessage = "default";
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }
