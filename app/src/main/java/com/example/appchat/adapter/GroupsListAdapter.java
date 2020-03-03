package com.example.appchat.adapter;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appchat.R;
import com.example.appchat.model.Account;
import com.example.appchat.model.Group;
import com.example.appchat.view.ChatsGrActivity;
import com.example.appchat.view.PlayMesageActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.ViewHolder> {

   private ArrayList<Group> groups;
   private Account account;

   public GroupsListAdapter(ArrayList<Group> groups, Account account) {
      this.groups = groups;
      this.account = account;
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_room,parent,false);
      return new ViewHolder(view);
   }
   @Override
   public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
      holder.txtRoomName.setText(groups.get(position).getName());

      holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), ChatsGrActivity.class);
            intent.putExtra("Conversation", groups.get(position));
            intent.putExtra("Account", account);
            v.getContext().startActivity(intent);
         }
      });

      if (groups.get(position).isChecked()){
         holder.container.setVisibility(View.VISIBLE);
      }else {
         holder.container.setVisibility(View.GONE);
      }

      holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
         @Override
         public boolean onLongClick(View v) {
            if (groups.get(position).isChecked()) {
               groups.get(position).setChecked(false);
               notifyItemChanged(position);
            }else {
               setCheckedOff();
               groups.get(position).setChecked(true);
               notifyItemChanged(position);
            }
            return false;
         }
      });
      holder.playShow.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), PlayMesageActivity.class);
            intent.putExtra("Conversation", groups.get(position));
            intent.putExtra("Account", account);
            v.getContext().startActivity(intent);
            holder.container.setVisibility(View.GONE);
         }
      });

      holder.deleteGroup.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            FirebaseDatabase.getInstance().getReference()
               .child("Conversation")
               .child(account.getUserName())
               .addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(DataSnapshot dataSnapshot) {
                     if (dataSnapshot.hasChildren()) {
                        DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                        firstChild.getRef().removeValue();
                        groups.remove(position);
                        notifyDataSetChanged();
                        holder.container.setVisibility(View.GONE);
                        Toast.makeText(holder.playShow.getContext(), "Delete Success", Toast.LENGTH_SHORT).show();
                     }
                  }
                  @Override
                  public void onCancelled(DatabaseError databaseError) {
                  }
               });
         }
      });
   }
   @Override
   public int getItemCount() {
      return groups.size();
   }

   class ViewHolder extends RecyclerView.ViewHolder{
      LinearLayout container;
      TextView txtRoomName;
      ImageView playShow, deleteGroup;
      ViewHolder(@NonNull View itemView) {
         super(itemView);
         txtRoomName = itemView.findViewById(R.id.txtRoomName);
         container = itemView.findViewById(R.id.container_row_gr);
         playShow = itemView.findViewById(R.id.playShow);
         deleteGroup = itemView.findViewById(R.id.deleteGroup);
      }
   }
   private void setCheckedOff(){
      for (int i = 0; i< groups.size(); i++){
         if (groups.get(i).isChecked()){
            groups.get(i).setChecked(false);
            notifyItemChanged(i);
         }
      }
   }

}
