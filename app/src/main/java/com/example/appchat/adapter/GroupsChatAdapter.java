package com.example.appchat.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.appchat.Constants;
import com.example.appchat.R;
import com.example.appchat.model.Account;
import com.example.appchat.model.ChatGroup;
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
   private ArrayList<ChatGroup> chatGroups;
   private Account account;

   public GroupsChatAdapter(ArrayList<ChatGroup> chatGroups, Account account) {
      this.chatGroups = chatGroups;
      this.account = account;
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
      ImageView imgPicture;
      CircleImageView avatar;

      ViewHolder(@NonNull View itemView,int type) {
         super(itemView);
         txtDate     = itemView.findViewById(R.id.txtDate);
         txtusername = itemView.findViewById(R.id.txtusername);
         avatar      = itemView.findViewById(R.id.item_profile_image);

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
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      String time = formatter.format(chatGroups.get(position).getDate());
      holder.txtDate.setText(time);
      holder.txtusername.setText(chatGroups.get(position).getUserName());

      if(account.getAvatar().equals("default")){
         holder.avatar.setImageResource(R.drawable.profile_image);
      }else {
         Glide.with(holder.avatar.getContext()).load(account.getAvatar()).into(holder.avatar);
      }

      if (chatGroups.get(position).getType() == Constants.TYPE_MESSAGE){
         holder.txtMessage.setText(chatGroups.get(position).getMessage());
      }else {
         Glide.with(holder.imgPicture.getContext())
            .load(chatGroups.get(position).getPath())
            .into(holder.imgPicture);
      }
   }
   @Override
   public int getItemCount() {
      return chatGroups.size();
   }
   @Override
   public int getItemViewType(int position) {
      if (chatGroups.get(position).getUserName().equals(account.getUserName())){
         if (chatGroups.get(position).getType() == Constants.TYPE_MESSAGE){
            return TYPE_SEND_MESSAGE;
         }else {
            return TYPE_SEND_PICTURE;
         }
      }else {
         if (chatGroups.get(position).getType() == Constants.TYPE_MESSAGE){
            return TYPE_RECEIVE_MESSAGE;
         }else {
            return TYPE_RECEIVE_PICTURE;
         }
      }
   }
}
