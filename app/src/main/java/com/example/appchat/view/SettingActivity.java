package com.example.appchat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {


   private Button updateAccountSetting;
   private EditText userName, userStatus;
   private CircleImageView userProfileImage;

   private FirebaseAuth mAuth;
   private String currentUserID;
   private DatabaseReference RootRef;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_setting);

      mAuth = FirebaseAuth.getInstance();
      currentUserID = mAuth.getCurrentUser().getUid();
      RootRef = FirebaseDatabase.getInstance().getReference();




      init();

      addControls();

      //userName.setVisibility(View.INVISIBLE);
      // lay thong tin user
      RetrieveUserInfor();
   }

   private void RetrieveUserInfor() {
      RootRef.child("Users").child(currentUserID)
         .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name"))&& (dataSnapshot.hasChild("image"))){
                  String retrieveUsername = dataSnapshot.child("name").getValue().toString();
                  String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                  String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();


                  userName.setText(retrieveUsername);
                  userStatus.setText(retrieveStatus);
               }
               else if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name"))){
                  String retrieveUsername = dataSnapshot.child("name").getValue().toString();
                  String retrieveStatus = dataSnapshot.child("status").getValue().toString();

                  userName.setText(retrieveUsername);
                  userStatus.setText(retrieveStatus);
               }
               else {
                  //userName.setVisibility(View.VISIBLE);
                  Toast.makeText(SettingActivity.this, "Please set and update your profile information...", Toast.LENGTH_SHORT).show();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
         });
   }

   private void addControls() {
      updateAccountSetting.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            UpdateSetting();
         }
      });
   }

   private void UpdateSetting() {
      String setUserName = userName.getText().toString();
      String setUserStatus = userStatus.getText().toString();
      
      if(TextUtils.isEmpty(setUserName)){
         Toast.makeText(this, "Please write your username first...", Toast.LENGTH_SHORT).show();
      }
      if(TextUtils.isEmpty(setUserStatus)){
         Toast.makeText(this, "Please write your status...", Toast.LENGTH_SHORT).show();
      }else {
         HashMap<String,String> profileMap = new HashMap<>();
         profileMap.put("uid", currentUserID);
         profileMap.put("name", setUserName);
         profileMap.put("status", setUserStatus);
         RootRef.child("Users").child(currentUserID).setValue(profileMap)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                  if(task.isSuccessful()){
                     SendUserToActivityMain();
                     Toast.makeText(SettingActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                  }else {
                     String message = task.getException().toString();
                     Toast.makeText(SettingActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                  }
               }
            });
      }

   }


   private void init() {
      updateAccountSetting = findViewById(R.id.update_setting_button);
      userName = findViewById(R.id.set_username);
      userStatus = findViewById(R.id.set_profile_status);
      userProfileImage = findViewById(R.id.set_profile_image);

   }
   private void SendUserToActivityMain() {
      Intent intent = new Intent(SettingActivity.this, MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);
      finish();
   }


}
