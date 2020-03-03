package com.example.appchat.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.appchat.R;
import com.example.appchat.adapter.GroupsListAdapter;
import com.example.appchat.model.Account;
import com.example.appchat.model.Group;
import com.example.appchat.view.HomeActivity;
import com.example.appchat.view.MainActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationFragment extends Fragment {
   private RecyclerView recyclerRoom;
   private Account account;
   private MainActivity mainActivity = (MainActivity) getActivity();
   private GroupsListAdapter groupsListAdapter;
   private ArrayList<Group> groups = new ArrayList<>();
   private AlertDialog alertDialog;
   private ImageButton btnAddNewGroups ;



   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_conversation, container, false);

      recyclerRoom = view.findViewById(R.id.recyclerConversation);
      setRecyclerConversation();

      btnAddNewGroups = view.findViewById(R.id.add_Conversation);
      btnAddNewGroups.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            createNewGroups();
         }
      });

      getRoomList();

      return view;
   }

   private void createNewGroups() {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      View container = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_groups,null);
      builder.setView(container);
      alertDialog = builder.create();

      final EditText edtRoomName = container.findViewById(R.id.edtRoomName);
      Button btnCreate = container.findViewById(R.id.btnCreate);

      btnCreate.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v) {
            if (TextUtils.isEmpty(edtRoomName.getText())){
               Toast.makeText(getActivity(), "Please enter name groups...", Toast.LENGTH_SHORT).show();
               return;
            }
            createGroups(edtRoomName.getText().toString());
         }
      });
      alertDialog.show();
   }
   private void createGroups(String groupsName) {
      String id = System.currentTimeMillis() + "";
      final Group group = new Group();
      group.setId(id);
      group.setName(groupsName);
      FirebaseDatabase.getInstance().getReference()
         .child("Conversation")
         //.child(myAccount.getUserName())
         .child(id)
         .setValue(group, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
               if (databaseError == null){
                  Toast.makeText(getActivity(), "Created groups successfully", Toast.LENGTH_SHORT).show();
                  if (alertDialog != null)
                  {
                     if (alertDialog.isShowing()){
                        alertDialog.cancel();
                     }
                  }
               }else {
                  Toast.makeText(getActivity(), "Error !!!", Toast.LENGTH_SHORT).show();
               }
            }
         });
   }
   public void passData() {


   }

   private void getRoomList() {
      FirebaseDatabase.getInstance().getReference()
         .child("Conversation")
         //.child(account.getUserName())
         .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               if (dataSnapshot.exists()){
                  Group group = dataSnapshot.getValue(Group.class);

                  groups.add(group);
                  groupsListAdapter.notifyItemInserted(groups.size() - 1);
               }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
         });
   }

   private void setRecyclerConversation() {
      recyclerRoom.setHasFixedSize(true);
      recyclerRoom.setLayoutManager(new LinearLayoutManager(getActivity()));
      groupsListAdapter = new GroupsListAdapter(groups,account);
      recyclerRoom.setAdapter(groupsListAdapter);
   }


}
