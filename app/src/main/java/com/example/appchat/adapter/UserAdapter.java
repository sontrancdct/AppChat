package com.example.appchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchat.R;
import com.example.appchat.login.ProfileActivity;
import com.example.appchat.model.Account;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

   private Context context;
   private List<Account> accounts;

   public UserAdapter(Context context, List<Account> accounts) {
      this.context = context;
      this.accounts = accounts;
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent,false);


      return new UserAdapter.ViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
      final Account account = accounts.get(position);
      holder.username.setText(account.getUserName());

      if (account.getAvatar().equals("default")) {
         holder.myAvatar.setImageResource(R.drawable.profile_image);
      } else {
         Glide.with(holder.myAvatar.getContext())
            .load(account.getAvatar())
            .into(holder.myAvatar);
      }

      holder.myAvatar.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("Account", accounts.get(position));
            context.startActivity(intent);
         }
      });
      }


      @Override
      public int getItemCount() {
         return accounts.size();
      }

      public class ViewHolder extends RecyclerView.ViewHolder{

         public TextView username;
         public ImageView profile_image;
         CircleImageView myAvatar;

         public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.item_user_name);
            myAvatar = itemView.findViewById(R.id.item_profile_image);

         }
      }
   }
