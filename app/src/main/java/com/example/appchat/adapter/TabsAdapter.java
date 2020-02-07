package com.example.appchat.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.appchat.fragment.ChatsFragment;
import com.example.appchat.fragment.ContactsFragment;
import com.example.appchat.fragment.GroupsFragment;

public class TabsAdapter extends FragmentPagerAdapter {


   public TabsAdapter(@NonNull FragmentManager fm) {
      super(fm);
   }

   @NonNull
   @Override
   public Fragment getItem(int position) {

      switch (position){
         case 0:
            ChatsFragment chatsFragment = new ChatsFragment();
            return  chatsFragment;
         case 1:
            GroupsFragment groupsFragment = new GroupsFragment();
            return  groupsFragment;
         case 2:
            ContactsFragment contactsFragment = new ContactsFragment();
            return  contactsFragment;

         default:
            return null;
      }
   }

   @Override
   public int getCount() {
      return 3;
   }

   @Override
   public CharSequence getPageTitle(int position)
   {
      switch (position){
         case 0:
            return "Chats";
         case 1:
            return "Groups";
         case 2:
            return "Contacts";

         default:
            return null;
      }
   }
}
