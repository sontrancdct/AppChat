package com.example.appchat.login;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.example.appchat.R;
import com.example.appchat.view.MainActivity;

public class LoginActivity extends AppCompatActivity {

   private EditText edtUsername,edtPassword;
   private ProgressDialog loadingBar;
   private CheckBox saveLogin;
   SharedPreferences sharedPreferences;

   String prefname ="data";
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);

      initView();
   }

   private void initView() {
      edtUsername = findViewById(R.id.edtUsername);
      edtPassword = findViewById(R.id.edtPassword);
      saveLogin = findViewById(R.id.save_login);
   }

   public void onClickSignup(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
   }

   public void onClickLogin(View view) {
      loadingBar = new ProgressDialog(LoginActivity.this);
      loadingBar.setTitle("Sign In...");
      loadingBar.setMessage("Please wait...");
      loadingBar.setCanceledOnTouchOutside(true);
      loadingBar.show();

      if (TextUtils.isEmpty(edtUsername.getText()) ||TextUtils.isEmpty(edtPassword.getText())){
         Toast.makeText(this, "Please complete all information", Toast.LENGTH_SHORT).show();
         loadingBar.dismiss();
         return;
      }
      sharedPreferences = getSharedPreferences("MyShared", MODE_PRIVATE);
      String name = edtUsername.getText().toString();
      String pass = edtPassword.getText().toString();
      
      //Account account = null;

      String username1 = sharedPreferences.getString("username", "no");
      String password2 = sharedPreferences.getString("password", "no");

      if(username1.equals(name)){
         if(password2.equals(pass)){
            Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //  intent.putExtra("Account", account);
            startActivity(intent);
            finish();
         }
         else {
            Toast.makeText(LoginActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
            return;
         }
      }
      else
      {
         Toast.makeText(LoginActivity.this, "Account not exists", Toast.LENGTH_SHORT).show();
         loadingBar.dismiss();
      }

//      FirebaseDatabase.getInstance().getReference()
//         .child("Account")
//         .child(edtUsername.getText().toString())
//         .addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//               if (!dataSnapshot.exists()){
//                  Toast.makeText(LoginActivity.this, "Account not exists", Toast.LENGTH_SHORT).show();
//                  loadingBar.dismiss();
//                  return;
//               }
//               Account account = dataSnapshot.getValue(Account.class);
//               if (!account.getPassword().equals(edtPassword.getText().toString())){
//                  Toast.makeText(LoginActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
//                  loadingBar.dismiss();
//                  return;
//               }
//               Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
//               loadingBar.dismiss();
//
//               Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//               intent.putExtra("Account", account);
//               startActivity(intent);
//               finish();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//         });
   }
   @Override
   protected void onPause() {
      super.onPause();
      savingPreferences();
   }

   private void savingPreferences() {
      SharedPreferences pre = getSharedPreferences(prefname, MODE_PRIVATE);
      SharedPreferences.Editor editor=pre.edit();
      String user = edtUsername.getText().toString();
      String pwd  = edtPassword.getText().toString();
      boolean bchk = saveLogin.isChecked();
      if(!bchk)
      {
         editor.clear();
      }
      else
      {
         editor.putString("user", user);
         editor.putString("pwd", pwd);
         editor.putBoolean("checked", bchk);
      }
      editor.apply();
   }

   @Override
   protected void onResume() {
      super.onResume();
      restoringPreferences();
   }

   private void restoringPreferences() {
      SharedPreferences pre = getSharedPreferences(prefname,MODE_PRIVATE);
      boolean bchk=pre.getBoolean("checked", false);
      if(bchk)
      {
         String user=pre.getString("user", "");
         String pwd=pre.getString("pwd", "");
         edtUsername.setText(user);
         edtPassword.setText(pwd);
      }
      saveLogin.setChecked(bchk);
   }
}
