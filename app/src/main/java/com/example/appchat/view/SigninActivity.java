package com.example.appchat.view;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SigninActivity extends AppCompatActivity {

   private EditText edtUsername,edtPassword;

   private ProgressDialog loadingBar;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_signin);

      initView();
   }

   private void initView() {
      edtUsername = findViewById(R.id.edtUsername);
      edtPassword = findViewById(R.id.edtPassword);
   }

   public void onClickSignup(View view) {
      startActivity(new Intent(this,SignupActivity.class));
   }

   public void onClickLogin(View view) {
      loadingBar = new ProgressDialog(SigninActivity.this);
      loadingBar.setTitle("Sign In...");
      loadingBar.setMessage("Please wait...");
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
      FirebaseDatabase.getInstance().getReference()
         .child("Account")
         .child(edtUsername.getText().toString())
         .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (!dataSnapshot.exists()){
                  Toast.makeText(SigninActivity.this, "Account not exists", Toast.LENGTH_SHORT).show();
                  loadingBar.dismiss();
                  return;
               }
               Account account = dataSnapshot.getValue(Account.class);
               if (!account.getPassword().equals(edtPassword.getText().toString())){
                  Toast.makeText(SigninActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                  loadingBar.dismiss();
                  return;
               }
               Toast.makeText(SigninActivity.this, "Login success", Toast.LENGTH_SHORT).show();
               loadingBar.dismiss();

               Intent intent = new Intent(SigninActivity.this, HomeActivity.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               intent.putExtra("Account", account);
               startActivity(intent);
               finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
         });
   }
}
