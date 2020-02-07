package com.example.appchat.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.appchat.R;
import com.example.appchat.view.GroupsChatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

   private View groupFragmentView;
   private ListView listView;
   private ArrayAdapter<String> arrayAdapter;
   private ArrayList<String> listGroups = new ArrayList<>();

   private DatabaseReference GroupRef;


   public GroupsFragment() {
      // Required empty public constructor
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {

      groupFragmentView=  inflater.inflate(R.layout.fragment_groups, container, false);

      GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups");



      init();
      RetrieveAndDisplayGroups();

      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String currentGroupName = parent.getItemAtPosition(position).toString();

            Intent intent = new Intent(getContext(), GroupsChatActivity.class);
            intent.putExtra("groupsName",currentGroupName);
            startActivity(intent);
         }
      });


      return  groupFragmentView;
   }

   private void init() {
      listView = groupFragmentView.findViewById(R.id.lv_groups);
      arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listGroups);
      listView.setAdapter(arrayAdapter);

   }
   private void RetrieveAndDisplayGroups() {
      GroupRef.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Set<String> set = new HashSet<>();
            Iterator iterator = (Iterator) dataSnapshot.getChildren().iterator();

            while (iterator.hasNext()){
               set.add((((DataSnapshot) iterator.next()).getKey()));
            }

            listGroups.clear();
            listGroups.addAll(set);
            arrayAdapter.notifyDataSetChanged();
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
      });
   }


}
