package com.example.appchat.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.R;
import com.example.appchat.appdata.AppChatUtil;
import com.example.appchat.model.Account;

public class RegisterActivity extends AppCompatActivity {
   private EditText edtUsername,edtPassword,edtRePassword;
   private ProgressDialog loadingBar;
   private TextView textView;

   SharedPreferences sharedPreferences ;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_register);
      sharedPreferences = getSharedPreferences("MyShared", MODE_PRIVATE);
      initView();

      textView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
         }
      });
   }
   private void initView() {
      edtUsername = findViewById(R.id.edtUsername);
      edtPassword = findViewById(R.id.edtPassword);
      edtRePassword = findViewById(R.id.edtRePassword);
      textView = findViewById(R.id.have_account);
   }

   public void onClickSignup(View view) {
      loadingBar = new ProgressDialog(RegisterActivity.this);
      loadingBar.setTitle("Creating new Account");
      loadingBar.setMessage("Please wait, while we creating new account for you...");
      loadingBar.setCanceledOnTouchOutside(true);
      loadingBar.show();

      if (TextUtils.isEmpty(edtUsername.getText()) || TextUtils.isEmpty(edtPassword.getText()) ||TextUtils.isEmpty(edtRePassword.getText())){
         Toast.makeText(this, "Please complete all information", Toast.LENGTH_SHORT).show();
         loadingBar.dismiss();
         return;
      }
      if (!edtPassword.getText().toString().equals(edtRePassword.getText().toString())){
         Toast.makeText(this, "password incorrect, please try again !", Toast.LENGTH_SHORT).show();
         loadingBar.dismiss();
         edtRePassword.setText("");
         return;
      }

      String Name = edtUsername.getText().toString();
      String Password = edtPassword.getText().toString();
      Account account = new Account();
      account.setUserName(Name);
      account.setPassword(Password);
      account.setAvatar("default");

      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putString("username", Name);
      editor.putString("password", Password);
      editor.apply();

      Toast.makeText(RegisterActivity.this, "Register Succesfully", Toast.LENGTH_SHORT).show();
      loadingBar.dismiss();
      edtUsername.setText("");
      edtPassword.setText("");
      edtRePassword.setText("");
      SignUpSuccess();
   }

   private void SignUpSuccess() {
      Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);
      finish();
   }
}
