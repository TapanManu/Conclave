package com.tman.conclave;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import android.view.View;


public class MainActivity extends AppCompatActivity implements MessageAdapter.ItemClicked {
    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    View customView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        myAdapter = new MessageAdapter(this,MessageList.messages);
        recyclerView.setAdapter(myAdapter);



    }


    @Override
    public void onItemClicked(int index) {

        Messages m = MessageList.messages.get(index);
        Intent intent = new Intent(MainActivity.this,ChatActivity.class);
        intent.putExtra("name",m.getName());

        MainActivity.this.startActivity(intent);
        //put extras to intent based on the index selected
    }

    public void notifyDataChanged(){
        myAdapter.notifyDataSetChanged();
    }

}