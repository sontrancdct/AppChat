package com.example.appchat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {


   private FirebaseUser currentUser;
   private Button btnRegister;
   private EditText edtEmail, edtPassword;
   private TextView tvHaveAccount;
   private DatabaseReference RootRef;

   private FirebaseAuth mAuth;

   private ProgressDialog loadingBar;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_register);

      mAuth = FirebaseAuth.getInstance();
      RootRef = FirebaseDatabase.getInstance().getReference();



      init();
      addControls();
   }

   private void addControls() {
      tvHaveAccount.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            SendUserToActivityLogin();
         }
      });


      btnRegister.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            CreateNewAccount();
         }
      });
   }

   private void SendUserToActivityLogin() {
      startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
   }

   private void CreateNewAccount() {
      String email = edtEmail.getText().toString();
      String password = edtPassword.getText().toString();

      if(TextUtils.isEmpty(email)){
         Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show();
      }
      if(TextUtils.isEmpty(password)){
         Toast.makeText(this, "Please enter password...", Toast.LENGTH_SHORT).show();
      }
      if(password.length()< 6){
         Toast.makeText(this, "Password must be greater than 6 characters", Toast.LENGTH_SHORT).show();
      }
      else {
         loadingBar.setTitle("Creating new Account");
         loadingBar.setMessage("Please wait, while we creating new account for you...");
         loadingBar.setCanceledOnTouchOutside(true);
         loadingBar.show();

         mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task) {
                  if(task.isSuccessful()){
                     String currentUserID = mAuth.getCurrentUser().getUid();
                     RootRef.child("Users").child(currentUserID).setValue("");

                     //SendUserToActivityLogin();
                     SendUserToActivityMain();
                     Toast.makeText(RegisterActivity.this, "Account Created Successfully !", Toast.LENGTH_SHORT).show();
                     loadingBar.dismiss();
                  }
                  else {
                     String message = task.getException().toString();
                     Toast.makeText(RegisterActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                     loadingBar.dismiss();
                  }
               }
            });
      }
   }

   private void SendUserToActivityMain() {
      Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);
      finish();
   }

   private void init() {
      edtEmail = findViewById(R.id.edt_register_email);
      edtPassword = findViewById(R.id.edt_register_pass);
      tvHaveAccount = findViewById(R.id.tv_already_have_an_account);
      btnRegister = findViewById(R.id.btn_register);
      loadingBar = new ProgressDialog(this);
   }
}