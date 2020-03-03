package com.example.appchat.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.appchat.R;
import com.example.appchat.adapter.GroupsListAdapter;
import com.example.appchat.adapter.TabsAdapter;
import com.example.appchat.fragment.ConversationFragment;
import com.example.appchat.model.Account;
import com.example.appchat.model.Group;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
   private RecyclerView recyclerRoom;
   private Account myAccount;
   private GroupsListAdapter groupsListAdapter;
   private ArrayList<Group> groups = new ArrayList<>();
   private AlertDialog alertDialog;

   private Toolbar toolbar;
   private ViewPager viewPager;
   private TabLayout tabLayout;
   private TabsAdapter tabsAdapter;



   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      toolbar = findViewById(R.id.toolBar);
      setSupportActionBar(toolbar);
      getSupportActionBar().setTitle("Chat++");
      toolbar.setVisibility(View.GONE);

      viewPager = findViewById(R.id.main_Viewpager);
      tabsAdapter = new TabsAdapter(getSupportFragmentManager());
      viewPager.setAdapter(tabsAdapter);

      tabLayout = findViewById(R.id.main_tabs);
      tabLayout.setupWithViewPager(viewPager);


      Bundle b = new Bundle();
      b.putString("Account", String.valueOf(myAccount));
      tabsAdapter.setData(b);


   }

}
