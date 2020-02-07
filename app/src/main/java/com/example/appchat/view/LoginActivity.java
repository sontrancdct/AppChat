package com.example.appchat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class LoginActivity extends AppCompatActivity {



   //private FirebaseUser currentUser;
   private Button btnLogin;
   private EditText edtEmail, edtPassword;
   private TextView tvForgetPass, tvRegister;

   private ProgressDialog loadingBar;

   private FirebaseAuth mAuth;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);

      mAuth = FirebaseAuth.getInstance();
      //currentUser = mAuth.getCurrentUser();

      init();
      addControls();
   }

   private void addControls() {
      tvRegister.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
         }
      });
      btnLogin.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            UserToLoginSuccess();
         }
      });
   }

   private void UserToLoginSuccess() {
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
         loadingBar.setTitle("Sign In...");
         loadingBar.setMessage("Please wait...");
         loadingBar.setCanceledOnTouchOutside(true);
         loadingBar.show();

         mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task) {
                  if(task.isSuccessful()){
                     SendUserToActivityMain();
                     Toast.makeText(LoginActivity.this, "Logged in Successfully !", Toast.LENGTH_SHORT).show();
                     loadingBar.dismiss();
                  }
                  else {
                     String message = task.getException().toString();
                     //Toast.makeText(LoginActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                     Toast.makeText(LoginActivity.this, "Email and Password are incorrect", Toast.LENGTH_SHORT).show();
                     loadingBar.dismiss();
                  }
               }
            });

      }
   }

   private void init() {
      edtEmail = findViewById(R.id.edt_login_email);
      edtPassword = findViewById(R.id.edt_login_pass);
      tvForgetPass = findViewById(R.id.tv_forgetpass);
      tvRegister = findViewById(R.id.tv_new_account);
      btnLogin = findViewById(R.id.btn_login);
      loadingBar = new ProgressDialog(this);
   }

//   @Override
//   protected void onStart() {
//      super.onStart();
//
//      if(currentUser != null){
//         SendUserToMainActivity();
//      }
//   }

   private void SendUserToActivityMain() {
      Intent intent = new Intent(LoginActivity.this, MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);
      finish();
   }
}
