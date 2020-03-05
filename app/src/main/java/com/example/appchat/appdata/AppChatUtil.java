package com.example.appchat.appdata;

import com.example.appchat.utils.DkJsonHelper;


public class AppChatUtil {


   public static void saveAppChatData(AppChatData appChatData) {
      String jsonData = DkJsonHelper.getIns().obj2json(appChatData);
      saveSharedPreferences(jsonData, "");
   }

   public static AppChatData loadAppChatData() {
      String jsonData = loadSharedPreferences("");
      if (jsonData == null) return new AppChatData();
      if (jsonData.length() <=0) return new AppChatData();

      AppChatData appChatData = DkJsonHelper.getIns().json2obj(jsonData, AppChatData.class);
      return appChatData;
   }

   public static void saveSharedPreferences(String jsonData, String key) {


   }

   public static String loadSharedPreferences(String key) {
      return "";
   }

}
