package com.example.appchat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.R;
import com.example.appchat.adapter.UserAdapter;
import com.example.appchat.model.Account;
import com.example.appchat.model.Group;
import com.example.appchat.view.ChatsGrActivity;
import com.example.appchat.view.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddNewUserDiglog extends Dialog {
   private ChatsGrActivity chatsGrActivity;
   private Button btnAddNewUser, btnAddNow;
   private EditText editText;
   private RecyclerView recyclerViewListUser;
   private ArrayList<Account> accounts = new ArrayList<>();
   private UserAdapter userAdapter;
   private Group group;
   Dialog alertDialog;


   public AddNewUserDiglog(@NonNull Context context) {
      super(context);
      chatsGrActivity = (ChatsGrActivity) context;
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      setContentView(R.layout.dialog_add_friend);

      btnAddNewUser = findViewById(R.id.btnCreateNewUser);
      btnAddNow = findViewById(R.id.btnAddNow);
      editText = findViewById(R.id.edtUsername);

      recyclerViewListUser = findViewById(R.id.recyclerview_listusers);
      recyclerViewListUser.setHasFixedSize(true);
      recyclerViewListUser.setLayoutManager(new LinearLayoutManager(getContext()));

      btnAddNow.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            String userName = editText.getText().toString();

            if (TextUtils.isEmpty(userName)){
               Toast.makeText(getContext(), "Please enter username!", Toast.LENGTH_SHORT).show();
               return;
            }
            checkAccount(userName);
         }
      });

      readUser();
   }

   private void checkAccount(final String userName) {
      FirebaseDatabase.getInstance().getReference()
         .child("Account")
         .child(userName)
         .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (!dataSnapshot.exists()){
                  Toast.makeText(getContext(), "Account not exists", Toast.LENGTH_SHORT).show();
                  return;
               }
               addFriendIntoMessage(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
         });

   }
   private void addFriendIntoMessage(String userName) {
      FirebaseDatabase.getInstance().getReference()
         .child("GroupsList")
         .child(userName)
         .child(group.getId())
         .setValue(group, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
               if (databaseError == null){
                  Toast.makeText(getContext(), "add friend success", Toast.LENGTH_SHORT).show();

                  if (alertDialog != null){
                     if (alertDialog.isShowing()){
                        alertDialog.cancel();
                     }
                  }
               }else {
                  Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
               }
            }
         });
   }

   private void readUser() {
      DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
      DatabaseReference usersdRef = rootRef.child("Account");
      ValueEventListener eventListener = new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
            accounts.clear();
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
               Account account = ds.getValue(Account.class);
               //if (!account.getUserName().equals(firebaseUser.getUid()))
               accounts.add(account);
            }
            userAdapter = new UserAdapter(getContext(), accounts);
            recyclerViewListUser.setAdapter(userAdapter);
         }
         @Override
         public void onCancelled(DatabaseError databaseError) {
         }
      };
      usersdRef.addListenerForSingleValueEvent(eventListener);
   }
}
