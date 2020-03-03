package com.example.appchat.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.appchat.R;
import com.example.appchat.adapter.GroupsListAdapter;
import com.example.appchat.login.ProfileActivity;
import com.example.appchat.login.LoginActivity;
import com.example.appchat.model.Account;
import com.example.appchat.model.Group;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
   private Toolbar toolbar;
   private RecyclerView recyclerRoom;
   private Account myAccount;
   private GroupsListAdapter groupsListAdapter;
   private ArrayList<Group> groups = new ArrayList<>();
   private AlertDialog alertDialog;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_home);

      setToolbar();
      getIntentData();
      setRecyclerRoom();
      getRoomList();

   }
   @Override
   protected void onStart() {
      super.onStart();
      if(myAccount == null)
      {
         GoToLogin();
      }
      else
      {
         VerifyUserExistance();
      }
   }
   private void VerifyUserExistance() {

   }
   private void GoToLogin() {
      startActivity(new Intent(HomeActivity.this, LoginActivity.class));
   }
   private void setRecyclerRoom() {
      recyclerRoom = findViewById(R.id.recyclerRoom);
      recyclerRoom.setHasFixedSize(true);
      recyclerRoom.setLayoutManager(new LinearLayoutManager(this));
      groupsListAdapter = new GroupsListAdapter(groups,myAccount);
      recyclerRoom.setAdapter(groupsListAdapter);
   }

   public void getIntentData() {
      myAccount = (Account) getIntent().getSerializableExtra("Account");
   }

   private void setToolbar() {
      toolbar = findViewById(R.id.toolBar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setTitle("Chat++");
   }

   private void getRoomList() {
      FirebaseDatabase.getInstance().getReference()
         .child("GroupsList")
         .child(myAccount.getUserName())
         .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               if (dataSnapshot.exists()){
                  Group group = dataSnapshot.getValue(Group.class);

                  groups.add(group);
                  groupsListAdapter.notifyItemInserted(groups.size() - 1);
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
   private void createGroups(String groupsName) {
      String id = System.currentTimeMillis() + "";
      final Group group = new Group();
      group.setId(id);
      group.setName(groupsName);
      FirebaseDatabase.getInstance().getReference()
         .child("GroupsList")
         .child(myAccount.getUserName())
         .child(id)
         .setValue(group, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
               if (databaseError == null){
                  Toast.makeText(HomeActivity.this, "Created groups successfully", Toast.LENGTH_SHORT).show();
                  if (alertDialog != null)
                  {
                     if (alertDialog.isShowing()){
                        alertDialog.cancel();
                     }
                  }
               }else {
                  Toast.makeText(HomeActivity.this, "Error !!!", Toast.LENGTH_SHORT).show();
               }
            }
         });
   }
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      super.onCreateOptionsMenu(menu);
      getMenuInflater().inflate(R.menu.options_menu, menu);
      return true;
   }
   @Override
   public boolean onOptionsItemSelected(@NonNull MenuItem item) {
      switch (item.getItemId()){
         case R.id.addGroups:
            addGroups();
            return true;
         case R.id.profile:
            GoToProfile();
            return true;
         case R.id.logout:
            GoToLogout();
            return true;
            default:
               return super.onOptionsItemSelected(item);
      }
   }

   private void addGroups() {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      View container = LayoutInflater.from(this).inflate(R.layout.dialog_add_groups,null);
      builder.setView(container);
      alertDialog = builder.create();

      final EditText edtRoomName = container.findViewById(R.id.edtRoomName);
      Button btnCreate = container.findViewById(R.id.btnCreate);

      btnCreate.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v) {
            if (TextUtils.isEmpty(edtRoomName.getText())){
               Toast.makeText(HomeActivity.this, "Please enter name groups...", Toast.LENGTH_SHORT).show();
               return;
            }
            createGroups(edtRoomName.getText().toString());
         }
      });
      alertDialog.show();
   }

   private void GoToLogout() {
      Intent profile = new Intent(HomeActivity.this, LoginActivity.class);
      profile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(profile);
   }
   private void GoToProfile() {
      Intent profile = new Intent(HomeActivity.this, ProfileActivity.class);
      //profile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      profile.putExtra("Account",myAccount);
      startActivity(profile);
   }
}
