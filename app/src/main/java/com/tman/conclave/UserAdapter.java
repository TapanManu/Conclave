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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<User> users;
    ItemClicked activity;
    String LastMessage,Lasttime;

    public interface ItemClicked{
        void onItemClicked(int index);
    }

    public UserAdapter(Context context, ArrayList<User> list){
        this.users = list;
        if(users==null){
            Log.d("error","hi");
        }
        this.activity = (ItemClicked) context;
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
       // Log.d("namesview",users.get(position).getName());
        holder.nameview.setText(users.get(position).getName());
        if(users.get(position).getImageURL().equals("default"))
            holder.prof.setImageResource(R.drawable.profile);
        else{
            Thread t = new Thread(){
                public void run(){
                    holder.prof.setImageBitmap(getBitmapFromURL(users.get(position).getImageURL().toString()));
                }
            };

            t.start();
        }
        String userid = users.get(position).getId();
        lastMessage(userid,holder.chat,holder.time);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

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

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }

    private void lastMessage(final String userid, final TextView last_msg, final TextView last_time) {
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
                        if(LastMessage.length()>30){
                            last_msg.setText(LastMessage.substring(0,30)+"...");
                        }
                        else{
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
