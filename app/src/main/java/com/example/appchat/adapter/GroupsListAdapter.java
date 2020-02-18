package com.example.appchat.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.R;
import com.example.appchat.model.Account;
import com.example.appchat.model.Room;
import com.example.appchat.view.ChatsGrActivity;

import java.util.ArrayList;

public class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.ViewHolder> {

   private ArrayList<Room> rooms;
   private Account account;

   public GroupsListAdapter(ArrayList<Room> rooms, Account account) {
      this.rooms = rooms;
      this.account = account;
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_room,parent,false);

      return new ViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
      holder.txtRoomName.setText(rooms.get(position).getName());


      holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), ChatsGrActivity.class);
            intent.putExtra("GroupsList",rooms.get(position));
            intent.putExtra("Account", account);
            v.getContext().startActivity(intent);
         }
      });
   }

   @Override
   public int getItemCount() {
      return rooms.size();
   }

   class ViewHolder extends RecyclerView.ViewHolder{

      TextView txtRoomName;

      ViewHolder(@NonNull View itemView) {
         super(itemView);

         txtRoomName = itemView.findViewById(R.id.txtRoomName);
      }
   }
}
