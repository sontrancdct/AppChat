package com.example.appchat.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.R;
import com.example.appchat.adapter.GroupsChatAdapter;
import com.example.appchat.model.Account;
import com.example.appchat.model.Message;
import com.example.appchat.model.Group;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PlayMesageActivity extends AppCompatActivity {

   private Toolbar toolBar;
   private Group group;
   private Account myAccount;
   private GroupsChatAdapter groupsChatAdapter;
   private ArrayList<Message> messages = new ArrayList<>();

   private RecyclerView recyclerView;

   int count = 0;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_play_mesage);
      setToolbar();
      getIntentData();
      getSupportActionBar().setTitle(group.getName());
      //timeStart();
      createRecycler();
      getMessage();

   }

   private void getIntentData() {
      group = (Group) getIntent().getSerializableExtra("GroupsList");
      myAccount   = (Account) getIntent().getSerializableExtra("Account");
   }

   private void timeStart() {
      final TextView textView = findViewById(R.id.tv_play);
      Thread thread = new Thread(){
         @Override
         public void run() {
            while (!isInterrupted())
            {
               try {
                  Thread.sleep(1000);

                  runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                        count++;
                        textView.setText(String.valueOf(count));
                     }
                  });

               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }
            super.run();
         }
      };
      thread.start();
   }

   private void setToolbar() {
      toolBar = findViewById(R.id.toolBar);
      setSupportActionBar(toolBar);
   }
   @Override
   public boolean onSupportNavigateUp() {
      onBackPressed();
      return true;
   }

   long delay;
   private void getMessage() {
      final Handler handler = new Handler();
      final int MIN = 1000;
      final int MAX = 3000;
      final Random r = new Random();
      final int value = r.nextInt((MAX - MIN) + 1) + MIN;

      FirebaseDatabase.getInstance().getReference()
         .child("Message")
         .child(group.getId())
         .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               if (dataSnapshot.exists()) {
                  final Message message = dataSnapshot.getValue(Message.class);

                  handler.postDelayed(new Runnable() {
                     @Override
                     public void run() {
                        messages.add(message);
                        groupsChatAdapter.notifyItemInserted(messages.size() - 1);
                        recyclerView.smoothScrollToPosition(messages.size() - 1);
                     }
                  }, (delay += value));
               }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
         });
   }

   private void createRecycler() {
      recyclerView = findViewById(R.id.recyclerPlayMessage);
      recyclerView.setHasFixedSize(true);
      LinearLayoutManager ll = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
      ll.setStackFromEnd(true);
      recyclerView.setLayoutManager(ll);
      groupsChatAdapter = new GroupsChatAdapter(messages,myAccount, group);
      recyclerView.setAdapter(groupsChatAdapter);
   }

}
