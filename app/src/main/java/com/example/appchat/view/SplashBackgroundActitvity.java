package com.example.appchat.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import com.example.appchat.login.LoginActivity;

public class SplashBackgroundActitvity extends AppCompatActivity {

   private static final int SPLASH_DISPLAY_TIME = 1000;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      new Handler().postDelayed(new Runnable() {
         public void run() {
            Intent intent = new Intent(SplashBackgroundActitvity.this, LoginActivity.class);
            startActivity(intent);
            finish();
         }
      }, SPLASH_DISPLAY_TIME);
   }
}
