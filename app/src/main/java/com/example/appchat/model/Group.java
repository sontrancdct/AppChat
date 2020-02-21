package com.example.appchat.model;

import java.io.Serializable;

public class Group implements Serializable {

   private String id;
   private String name;

   boolean isChecked = false;

   public Group() {
   }

   public boolean isChecked() {
      return isChecked;
   }

   public void setChecked(boolean checked) {
      isChecked = checked;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
