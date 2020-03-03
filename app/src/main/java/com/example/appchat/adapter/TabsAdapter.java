package com.example.appchat.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.appchat.fragment.ConversationFragment;
import com.example.appchat.fragment.UserFragment;

public class TabsAdapter extends FragmentPagerAdapter {

   ConversationFragment conversationFragment;
   UserFragment userFragment;

   public TabsAdapter(@NonNull FragmentManager fm) {
      super(fm);
      conversationFragment = new ConversationFragment();
      userFragment = new UserFragment();
   }

   public void setData(Bundle b) {
      conversationFragment.passData();
      userFragment.passData();
   }

   @NonNull
   @Override
   public Fragment getItem(int position) {

      switch (position){
         case 0:
            return  conversationFragment;
         case 1:
            return  userFragment;

         default:
            return null;
      }
   }

   @Override
   public int getCount() {
      return 2;
   }

   @Override
   public CharSequence getPageTitle(int position)
   {
      switch (position){
         case 0:
            return "Conversation";
         case 1:
            return "Users";
         default:
            return null;
      }
   }
}

