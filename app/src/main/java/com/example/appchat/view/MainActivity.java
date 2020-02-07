package com.example.appchat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appchat.R;
import com.example.appchat.adapter.TabsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

   private Toolbar toolbar;
   private ViewPager viewPager;
   private TabLayout tabLayout;
   private TabsAdapter tabsAdapter;

   private FirebaseUser currentUser;
   private FirebaseAuth mAuth;

   private DatabaseReference RootRef;



   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mAuth = FirebaseAuth.getInstance();
      currentUser = mAuth.getCurrentUser();
      RootRef = FirebaseDatabase.getInstance().getReference();

      toolbar = findViewById(R.id.main_page_toolbar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setTitle("AppChat");


      viewPager = findViewById(R.id.main_Viewpager);
      tabsAdapter = new TabsAdapter(getSupportFragmentManager());
      viewPager.setAdapter(tabsAdapter);

      tabLayout = findViewById(R.id.main_tabs);
      tabLayout.setupWithViewPager(viewPager);
   }

   @Override
   protected void onStart() {
      super.onStart();

      if(currentUser == null){
         SendUserToLoginActivity();
      }
      else {
         VerifyUserExistance();
      }
   }

   private void VerifyUserExistance() {
         String currentUserID = mAuth.getCurrentUser().getUid();

         RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if((dataSnapshot.child("name").exists())){
                  //Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
               }else {
                  SendUserToSettingActivity();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
         });

   }

   private void SendUserToLoginActivity() {
      Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
      loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(loginIntent);
      finish();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      super.onCreateOptionsMenu(menu);

      getMenuInflater().inflate(R.menu.options_menu, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(@NonNull MenuItem item) {
      super.onOptionsItemSelected(item);
         if(item.getItemId() == R.id.Logout){
            mAuth.signOut();
            SendUserToLoginActivity();
         }
         if (item.getItemId() == R.id.setting){
            SendUserToSettingActivity();
         }
         if(item.getItemId() == R.id.find_friends){

         }
         if(item.getItemId() == R.id.createGroups){
            RequestNewGroups();
         }

      return true;
   }

   private void RequestNewGroups() {
      AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
      builder.setTitle("Enter Group name: ");

      final EditText groupNameField = new EditText(MainActivity.this);
      groupNameField.setBackgroundResource(R.drawable.bg_edittext);
      groupNameField.setHint("e.g Developer...");
      builder.setView(groupNameField);

      builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            String groupName = groupNameField.getText().toString();

            if(TextUtils.isEmpty(groupName)){
               Toast.makeText(MainActivity.this, "Please write group name...", Toast.LENGTH_SHORT).show();
            }
            else {
               CreateNewGroup(groupName);
            }
         }
      });
      builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
           dialog.cancel();
         }
      });
      builder.show();

   }

   private void CreateNewGroup(final String groupName) {
         RootRef.child("Groups").child(groupName).setValue("")
            .addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                  if(task.isSuccessful()){
                     Toast.makeText(MainActivity.this,groupName+ " Group is Created Successfully", Toast.LENGTH_SHORT).show();
                  }
                  else {

                  }
               }
            });

   }

   private void SendUserToSettingActivity() {
      Intent loginIntent = new Intent(MainActivity.this,SettingActivity.class);
      loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(loginIntent);
      finish();
   }
}
