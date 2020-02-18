package com.example.appchat.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appchat.R;
import com.example.appchat.model.Account;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {
   private EditText edtUsername,edtPassword,edtRePassword;
   private ProgressDialog loadingBar;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_signup);

      initView();
   }

   private void initView() {
      edtUsername = findViewById(R.id.edtUsername);
      edtPassword = findViewById(R.id.edtPassword);
      edtRePassword = findViewById(R.id.edtRePassword);
   }

   public void onClickSignup(View view) {
      loadingBar = new ProgressDialog(SignupActivity.this);
      loadingBar.setTitle("Creating new Account");
      loadingBar.setMessage("Please wait, while we creating new account for you...");
      loadingBar.setCanceledOnTouchOutside(true);
      loadingBar.show();


      if (TextUtils.isEmpty(edtUsername.getText())){
         Toast.makeText(this, "You have not entered your account name", Toast.LENGTH_SHORT).show();
         return;
      }

      if (TextUtils.isEmpty(edtPassword.getText())){
         Toast.makeText(this, "You have not entered the password", Toast.LENGTH_SHORT).show();
         return;
      }

      if (TextUtils.isEmpty(edtRePassword.getText())){
         Toast.makeText(this, "You have not entered the password", Toast.LENGTH_SHORT).show();
         return;
      }

      if (!edtPassword.getText().toString().equals(edtRePassword.getText().toString())){
         Toast.makeText(this, "password incorrect, please try again !", Toast.LENGTH_SHORT).show();
         edtRePassword.setText("");
         return;
      }
      FirebaseDatabase.getInstance().getReference()
         .child("Account")
         .child(edtUsername.getText().toString())
         .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()){
                  Toast.makeText(SignupActivity.this, "Account already exists", Toast.LENGTH_SHORT).show();
                  loadingBar.dismiss();
                  return;
               }
               signUpAccount();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
         });
   }
   private void signUpAccount() {

      Account account = new Account();
      account.setUserName(edtUsername.getText().toString());
      account.setPassword(edtPassword.getText().toString());
      account.setAvatar("default");

      FirebaseDatabase.getInstance().getReference()
         .child("Account")
         .child(edtUsername.getText().toString())
         .setValue(account, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
               if (databaseError != null){
                  Toast.makeText(SignupActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                  loadingBar.dismiss();
                  return;
               }
               Toast.makeText(SignupActivity.this, "Register Succesfully", Toast.LENGTH_SHORT).show();
               loadingBar.dismiss();
               edtUsername.setText("");
               edtPassword.setText("");
               edtRePassword.setText("");
               SignUpSuccess();
            }
         });
   }

   private void SignUpSuccess() {
      Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);
      finish();
   }

}
