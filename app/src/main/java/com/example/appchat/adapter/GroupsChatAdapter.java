package com.example.appchat.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.appchat.Constants;
import com.example.appchat.R;
import com.example.appchat.model.Account;
import com.example.appchat.model.Group;
import com.example.appchat.model.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsChatAdapter extends RecyclerView.Adapter<GroupsChatAdapter.ViewHolder> {
   private static final int TYPE_SEND_MESSAGE = 1;
   private static final int TYPE_SEND_PICTURE = 2;
   private static final int TYPE_RECEIVE_MESSAGE = 3;
   private static final int TYPE_RECEIVE_PICTURE = 4;

   //private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
   private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
   private ArrayList<Message> messages;
   private Account account;
   private Group group;

   public GroupsChatAdapter(ArrayList<Message> messages, Account account, Group group) {
      this.messages = messages;
      this.account = account;
      this.group = group;
   }
   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View container = null;

      switch (viewType){
         case TYPE_SEND_MESSAGE:
            container = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_send_message,parent,false);
            break;
         case TYPE_SEND_PICTURE:
            container = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_send_image,parent,false);
            break;
         case TYPE_RECEIVE_MESSAGE:
            container = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_receive_message,parent,false);
            break;
         case TYPE_RECEIVE_PICTURE:
            container = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_receive_image,parent,false);
            break;
      }
      return new ViewHolder(container, viewType);
   }
   class ViewHolder extends RecyclerView.ViewHolder{
      TextView txtMessage,txtDate, txtusername;
      ImageView imgPicture, delete;
      CircleImageView myAvatar;

      ViewHolder(@NonNull View itemView,int type) {
         super(itemView);
         txtDate     = itemView.findViewById(R.id.txtDate);
         txtusername = itemView.findViewById(R.id.txtusername);
         myAvatar      = itemView.findViewById(R.id.item_profile_image);
         delete      = itemView.findViewById(R.id.delete_item_mesage);

         switch (type){
            case TYPE_SEND_MESSAGE:
            case TYPE_RECEIVE_MESSAGE:
               txtMessage  = itemView.findViewById(R.id.txtMessage);
               break;
            case TYPE_SEND_PICTURE:
            case TYPE_RECEIVE_PICTURE:
               imgPicture  = itemView.findViewById(R.id.imgPicture);
               break;
         }
      }
   }
   @Override
   public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
      String time = formatter.format(messages.get(position).getDate());
      holder.txtDate.setText(time);
      holder.txtusername.setText(messages.get(position).getUserName());

      if (messages.get(position).getUserName().equals(account.getUserName())) {
         if (account.getAvatar().equals("default")) {
            holder.myAvatar.setImageResource(R.drawable.profile_image);
         } else {
            Glide.with(holder.myAvatar.getContext())
               .load(account.getAvatar())
               .into(holder.myAvatar);
         }
      }
      else {
         if (account.getAvatar().equals("default")) {
            holder.myAvatar.setImageResource(R.drawable.profile_image);
         } else {
            Glide.with(holder.myAvatar.getContext())
               .load(messages.get(position).getAvatar())
               .into(holder.myAvatar);
         }
      }

      if (messages.get(position).getType() == Constants.TYPE_MESSAGE){
         holder.txtMessage.setText(messages.get(position).getMessage());
      }else {
         Glide.with(holder.imgPicture.getContext())
            .load(messages.get(position).getPath())
            .into(holder.imgPicture);
      }

      if (messages.get(position).isChecked()){
         holder.delete.setVisibility(View.VISIBLE);
      }else {
         holder.delete.setVisibility(View.GONE);
      }
      holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
         @Override
         public boolean onLongClick(View v) {
            if (messages.get(position).isChecked()) {
               messages.get(position).setChecked(false);
               notifyItemChanged(position);
            }else {
               setCheckedOff();
               messages.get(position).setChecked(true);
               notifyItemChanged(position);
            }
            return false;
         }
      });

      holder.delete.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            FirebaseDatabase.getInstance().getReference()
               .child("Message")
               .child(group.getId())
               .addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(DataSnapshot dataSnapshot) {
                     if (dataSnapshot.hasChildren()) {
                        DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                        firstChild.getRef().removeValue();
                        messages.remove(position);
                        notifyDataSetChanged();
                        holder.delete.setVisibility(View.GONE);
                        Toast.makeText(holder.delete.getContext(), "Delete Success", Toast.LENGTH_SHORT).show();
                     }
                  }
                  @Override
                  public void onCancelled(DatabaseError databaseError) {
                  }
               });
         }
      });
   }

   private void setCheckedOff(){
      for (int i = 0; i< messages.size(); i++){
         if (messages.get(i).isChecked()){
            messages.get(i).setChecked(false);
            notifyItemChanged(i);
         }
      }
   }

   @Override
   public int getItemCount() {
      return messages.size();
   }
   @Override
   public int getItemViewType(int position) {
      if (messages.get(position).getUserName().equals(account.getUserName())){
         if (messages.get(position).getType() == Constants.TYPE_MESSAGE){
            return TYPE_SEND_MESSAGE;
         }else {
            return TYPE_SEND_PICTURE;
         }
      }else {
         if (messages.get(position).getType() == Constants.TYPE_MESSAGE){
            return TYPE_RECEIVE_MESSAGE;
         }else {
            return TYPE_RECEIVE_PICTURE;
         }
      }
   }
}
