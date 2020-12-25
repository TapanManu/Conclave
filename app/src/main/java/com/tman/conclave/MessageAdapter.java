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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private ArrayList<User> users;
    ItemClicked activity;

    public interface ItemClicked{
        void onItemClicked(int index);
    }

    public MessageAdapter(Context context, ArrayList<User> list){
        this.users = list;
        if(users==null){
            Log.d("error","hi");
        }
        this.activity = (ItemClicked) context;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messagelayout,viewGroup,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(users.get(position));
        holder.nameview.setText(users.get(position).getName());
        /*URL url = users.get(position).getURL();
        final Bitmap[] bmp = new Bitmap[1];

        Thread t = new Thread() {               //passing network operations on another thread
            public void run() {
                try {
                    bmp[0] = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
        holder.prof.setImageBitmap(bmp[0]);
        */
    }

    @Override
    public int getItemCount() {


        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameview;
        ImageView prof;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameview = itemView.findViewById(R.id.tvName);
            prof = itemView.findViewById(R.id.ivProf);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.onItemClicked(users.indexOf(view.getTag()));
                }
            });
        }
    }
}
