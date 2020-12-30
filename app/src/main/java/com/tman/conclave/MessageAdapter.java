package com.tman.conclave;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    FirebaseUser fuser;

    public static final int SEND_CHAT = 0;
    public static final int RECIEVE_CHAT = 1;

    int selectedPosition=-1;
    boolean down = false;

    Context currentcontext;

    private ArrayList<Message> messages;






    public MessageAdapter(Context context, ArrayList<Message> list){
        this.messages = list;
        if(messages==null){
            Log.d("error","hi");
        }
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v;
        if(viewType==SEND_CHAT)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.send_chat,viewGroup,false);
        else
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.receive_chat,viewGroup,false);
        return new ViewHolder((LinearLayout) v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(messages.get(position));
        holder.messageview.setText(messages.get(position).getMessage());
        holder.timeview.setText(messages.get(position).getTime());

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    selectedPosition=position;
                    down = true;
                    //Toast.makeText(currentcontext,"CLICKED", Toast.LENGTH_SHORT).show();
                    if(getItemViewType(position) == SEND_CHAT) {
                        if (selectedPosition == position){
                            //holder.layoutsend.setAlpha(0.4f);
                            holder.layoutsend.setBackgroundColor(Color.parseColor("#9de1e3"));
                        }
                    }
                    if(getItemViewType(position) == RECIEVE_CHAT){
                        if(selectedPosition == position) {
                            //holder.layoutreceive.setAlpha(0.4f);
                            holder.layoutreceive.setBackgroundColor(Color.parseColor("#9de1e3"));
                        }
                    }
                    return true;
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    if(getItemViewType(position) == SEND_CHAT) {
                        if (selectedPosition == position){
                            //holder.layoutsend.setAlpha(0);
                            holder.layoutsend.setBackgroundColor(Color.parseColor("#00ffffff"));

                        }
                    }
                    if(getItemViewType(position) == RECIEVE_CHAT){
                        if(selectedPosition == position) {
                            //holder.layoutreceive.setAlpha(0.4f);
                            holder.layoutreceive.setBackgroundColor(Color.parseColor("#00ffffff"));

                        }
                    }
                    return false;
                }

                return false;
            }

        });


    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(messages.get(position).getSender().equals(fuser.getUid())){
            return SEND_CHAT;
        }
        else{
            return RECIEVE_CHAT;
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView messageview;
        TextView timeview;
        LinearLayout layoutsend,layoutreceive;

        public ViewHolder(@NonNull LinearLayout itemView) {
            super(itemView);
            messageview = itemView.findViewById(R.id.messageview);
            timeview = itemView.findViewById(R.id.timeview);
            layoutsend = itemView.findViewById(R.id.send);
            layoutreceive = itemView.findViewById(R.id.receive);
            }
        }
    }
