package com.example.appchat.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupsChatActivity extends AppCompatActivity {

   private Toolbar toolbar;
   private ImageButton sendMessageButton;
   private EditText messageInput;
   private ScrollView scrollView;
   private TextView textMessage;

   private FirebaseAuth mAuth;
   private DatabaseReference UserRef, GroupnameRef, GroupMessageKeyRef;

   private String currentGroupName, currentUserID, currentUserName, currentDate, currentTime;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_groups_chat);


      currentGroupName = getIntent().getExtras().get("groupsName").toString();
      //Toast.makeText(GroupsChatActivity.this, currentGroupName+ "", Toast.LENGTH_SHORT).show();


      mAuth = FirebaseAuth.getInstance();
      currentUserID = mAuth.getCurrentUser().getUid();

      UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
      GroupnameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);



      init();
      
      
      getUserInfo();
      sendMessageButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            SaveMessageInfoToDatabase();

            messageInput.setText("");

            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
         }
      });


   }

   @Override
   protected void onStart() {
      super.onStart();

      GroupnameRef.addChildEventListener(new ChildEventListener() {
         @Override
         public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if(dataSnapshot.exists()){
               DisplayMessage(dataSnapshot);
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

   private void DisplayMessage(DataSnapshot dataSnapshot) {

      Iterator iterator = dataSnapshot.getChildren().iterator();

      while (iterator.hasNext()){
         String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
         String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
         String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
         String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();


         textMessage.append(chatName + " :\n" + chatMessage + "\n" + chatTime + "   " + chatDate + "\n\n\n");

         scrollView.fullScroll(ScrollView.FOCUS_DOWN);
      }

   }

   private void SaveMessageInfoToDatabase() {
      String message = messageInput.getText().toString();
      String messageKEY = GroupnameRef.push().getKey();

      if(TextUtils.isEmpty(message)){
         Toast.makeText(GroupsChatActivity.this, "Please write message...", Toast.LENGTH_SHORT).show();
      }
      else {

         Calendar calendarFordate = Calendar.getInstance();
         SimpleDateFormat currentDateformat = new SimpleDateFormat("dd-MM-yyyy");
         currentDate = currentDateformat.format(calendarFordate.getTime());

         Calendar calendarFortime = Calendar.getInstance();
         SimpleDateFormat currentTimeformat = new SimpleDateFormat("hh:mm a");
         currentTime = currentTimeformat.format(calendarFortime.getTime());

         HashMap<String, Object> groupMessageKey = new HashMap<>();
         GroupnameRef.updateChildren(groupMessageKey);

         GroupMessageKeyRef = GroupnameRef.child(messageKEY);

         HashMap<String, Object> messageInfoMap = new HashMap<>();
               messageInfoMap.put("name", currentUserName);
               messageInfoMap.put("message", message);
               messageInfoMap.put("date", currentDate);
               messageInfoMap.put("time", currentTime);
               GroupMessageKeyRef.updateChildren(messageInfoMap);


      }

   }

   private void getUserInfo() {
      UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
               currentUserName = dataSnapshot.child("name").getValue().toString();

            }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
      });


   }

   private void init() {
      toolbar = findViewById(R.id.groups_chat_bar_layout);
      setSupportActionBar(toolbar);
      getSupportActionBar().setTitle(currentGroupName);

      sendMessageButton = findViewById(R.id.send_message_button);
      messageInput = findViewById(R.id.edt_input_group_message);
      textMessage = findViewById(R.id.group_chat_text);
      scrollView = findViewById(R.id.my_scroll_view);



   }
}
