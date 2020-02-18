package com.example.appchat.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.appchat.Constants;
import com.example.appchat.R;
import com.example.appchat.adapter.GroupsChatAdapter;
import com.example.appchat.model.Account;
import com.example.appchat.model.ChatGroup;
import com.example.appchat.model.Room;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class ChatsGrActivity extends AppCompatActivity {

   private static final int REQUEST_PERMISSION = 113;
   private static final int REQUEST_CODE_PICK_PHOTO = 123;

   private Toolbar toolBar;
   private EditText edtMessage;
   private RecyclerView recyclerMessage;
   private GroupsChatAdapter groupsChatAdapter;
   private ArrayList<ChatGroup> chatGroups = new ArrayList<>();
   private Account myAccount;
   private Room room;
   private AlertDialog alertDialog;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_chats_gr);

      init();
      setUpToolBar();
      getIntentData();
      updateToolbar();
      createRecycler();
      getMessage();
   }

   private void getMessage() {
      FirebaseDatabase.getInstance().getReference()
         .child("ChatGroup")
         .child(room.getId())
         .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               if (dataSnapshot.exists()){
                  ChatGroup chatGroup = dataSnapshot.getValue(ChatGroup.class);

                  chatGroups.add(chatGroup);
                  groupsChatAdapter.notifyItemInserted(chatGroups.size() - 1);
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
      recyclerMessage.setHasFixedSize(true);
      recyclerMessage.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
      groupsChatAdapter = new GroupsChatAdapter(chatGroups,myAccount);
      recyclerMessage.setAdapter(groupsChatAdapter);
   }

   private void updateToolbar() {
      getSupportActionBar().setTitle(room.getName());
   }

   private void getIntentData() {
      room        = (Room) getIntent().getSerializableExtra("GroupsList");
      myAccount   = (Account) getIntent().getSerializableExtra("Account");
   }

   private void setUpToolBar() {
      setSupportActionBar(toolBar);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
   }

   private void init() {
      toolBar = findViewById(R.id.toolBar);
      edtMessage      = findViewById(R.id.edtMessage);
      recyclerMessage = findViewById(R.id.recyclerMessage);
   }
   public void onClickSentMessage(View view) {
      String message = edtMessage.getText().toString();

      if (TextUtils.isEmpty(message)){
         return;
      }
      long time = System.currentTimeMillis();

      ChatGroup chatGroup = new ChatGroup();
      chatGroup.setDate(time);
      chatGroup.setMessage(message);
      chatGroup.setUserName(myAccount.getUserName());
      chatGroup.setType(Constants.TYPE_MESSAGE);
      FirebaseDatabase.getInstance().getReference()
         .child("ChatGroup")
         .child(room.getId())
         .child(time + "")
         .setValue(chatGroup);

      edtMessage.setText("");
   }

   public void onClickAdd(MenuItem item) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      View container = LayoutInflater.from(this).inflate(R.layout.dialog_add_friend,null);
      builder.setView(container);
      alertDialog = builder.create();

      final EditText edtUsername    = container.findViewById(R.id.edtUsername);
      Button btnCreate        = container.findViewById(R.id.btnCreate);

      btnCreate.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            String userName = edtUsername.getText().toString();

            if (TextUtils.isEmpty(userName)){
               Toast.makeText(ChatsGrActivity.this, "Please enter username !", Toast.LENGTH_SHORT).show();
               return;
            }

            checkAccount(userName);
         }
      });

      alertDialog.show();
   }

   private void checkAccount(final String userName) {
      FirebaseDatabase.getInstance().getReference()
         .child("Account")
         .child(userName)
         .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (!dataSnapshot.exists()){
                  Toast.makeText(ChatsGrActivity.this, "Account not exists", Toast.LENGTH_SHORT).show();
                  return;
               }
               addFriendIntoMessage(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
         });
   }

   private void addFriendIntoMessage(String userName) {
      FirebaseDatabase.getInstance().getReference()
         .child("GroupsList")
         .child(userName)
         .child(room.getId())
         .setValue(room, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
               if (databaseError == null){
                  Toast.makeText(ChatsGrActivity.this, "add friend success", Toast.LENGTH_SHORT).show();

                  if (alertDialog != null){
                     if (alertDialog.isShowing()){
                        alertDialog.cancel();
                     }
                  }
               }else {
                  Toast.makeText(ChatsGrActivity.this, "failed", Toast.LENGTH_SHORT).show();
               }
            }
         });
   }
   @Override
   public boolean onOptionsItemSelected(@NonNull MenuItem item) {
      if (item.getItemId() == android.R.id.home){
         finish();
      }
      return super.onOptionsItemSelected(item);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_add,menu);
      return super.onCreateOptionsMenu(menu);
   }

   public void onClickOpenLibrary(View view) {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
         pickPicture();
         return;
      }
      if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
         pickPicture();
         return;
      }
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
   }

   private void pickPicture() {
      Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
      intent.setType("image/*");
      startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      if (requestCode == REQUEST_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
         pickPicture();
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

      if (requestCode == REQUEST_CODE_PICK_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
         FirebaseStorage.getInstance().getReference()
            .child(System.currentTimeMillis() + ".png")
            .putFile(data.getData())
            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                  String pathPicture = task.getResult().getDownloadUrl().toString();

                  long time = System.currentTimeMillis();

                  ChatGroup chatGroup = new ChatGroup();
                  chatGroup.setDate(time);
                  chatGroup.setPath(pathPicture);
                  chatGroup.setUserName(myAccount.getUserName());
                  chatGroup.setType(Constants.TYPE_PICTURE);

                  FirebaseDatabase.getInstance().getReference()
                     .child("ChatGroup")
                     .child(room.getId())
                     .child(time + "")
                     .setValue(chatGroup);
               }
            })
            .addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
               }
            });
      }
   }
}
