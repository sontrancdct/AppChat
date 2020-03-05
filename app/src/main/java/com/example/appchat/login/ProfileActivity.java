package com.example.appchat.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appchat.R;
import com.example.appchat.model.Account;
import com.example.appchat.view.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

   private CircleImageView imageViewProfile;
   private TextView tvUsername;
   private ProgressDialog loadingBar;
   private Toolbar toolbar;
   StorageReference storageReference;
   private static final int IMAGE_REQUEST = 1;
   public Uri imageUri;
   private Account account;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_profile);

      loadingBar = new ProgressDialog(this);

      setUpToolbar();

      imageViewProfile = findViewById(R.id.set_profile_image);
      tvUsername = findViewById(R.id.set_username);
      storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");
      account = (Account) getIntent().getSerializableExtra("Account");
      FirebaseDatabase.getInstance().getReference("Account")
         .child(account.getUserName())
         .addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            account = dataSnapshot.getValue(Account.class);
            assert account != null;
            tvUsername.setText(account.getUserName());

            if(account.getAvatar().equals("default")){
               imageViewProfile.setImageResource(R.drawable.profile_image);
            }else {
               Glide.with(getApplicationContext()).load(account.getAvatar()).into(imageViewProfile);
            }
         }
         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
      });

      imageViewProfile.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            OpenImage();
         }
      });
   }

   @Override
   public void onBackPressed() {
      super.onBackPressed();
   }

   private void setUpToolbar() {
      toolbar = findViewById(R.id.toolBar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setTitle("Profile");
//      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//      getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
   }

   private void OpenImage() {
      Intent intent = new Intent();
      intent.setType("image/*");
      intent.setAction(Intent.ACTION_GET_CONTENT);
      startActivityForResult(intent, IMAGE_REQUEST);
   }
   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data!=null)
      {
         imageUri = data.getData();
         CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1,1)
            .start(this);
      }
      if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
         CropImage.ActivityResult result = CropImage.getActivityResult(data);

         if(resultCode == RESULT_OK)
         {
            loadingBar.setTitle("Set Profile Image");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            Uri resultUri = result.getUri();

            final StorageReference filepath = storageReference.child(account.getUserName() + ".jpg");

            filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                  if(task.isSuccessful())
                  {
                     final String downloadUrl = task.getResult().getDownloadUrl().toString();
                     FirebaseDatabase.getInstance().getReference().child("Account").child(account.getUserName()).child("avatar")
                        .setValue(downloadUrl)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful())
                              {
                                 Toast.makeText(ProfileActivity.this, "Profile Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                 loadingBar.dismiss();
                                 startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                              }
                              else
                              {
                                 String message = task.getException().toString();
                                 Toast.makeText(ProfileActivity.this, "Error"+ message, Toast.LENGTH_SHORT).show();
                                 loadingBar.dismiss();
                              }
                           }
                        });
                  }
                  else
                  {
                     String message = task.getException().toString();
                     Toast.makeText(ProfileActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                     loadingBar.dismiss();
                  }
               }
            });
         }

      }
   }
}
