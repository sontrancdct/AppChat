package com.example.appchat.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appchat.R;
import com.example.appchat.adapter.UserAdapter;
import com.example.appchat.model.Account;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {
   private RecyclerView recyclerView;

   private UserAdapter userAdapter;
   private ArrayList<Account> accounts = new ArrayList<>();

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {

      View view = inflater.inflate(R.layout.fragment_user, container, false);
      recyclerView = view.findViewById(R.id.recyclerview_users);
      recyclerView.setHasFixedSize(true);
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

      readUser();



      return view;
   }

   private void readUser() {
      final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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

               recyclerView.setAdapter(userAdapter);
            }
         @Override
         public void onCancelled(DatabaseError databaseError) {

         }
      };
      usersdRef.addListenerForSingleValueEvent(eventListener);
   }

   public void passData() {
   }

}
