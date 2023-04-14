package com.example.billstracker;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;

public class SaveUserData {

    public void saveUserData (Context context, Login thisUser) {

        SharedPreferences sp = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(thisUser);
        editor.putString("thisUser", json);
        editor.apply();
    }
    public Login loadUserData (Context context) {

        SharedPreferences sp = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("thisUser", null);
        Type type = new TypeToken<Login>() {}.getType();
        return gson.fromJson(json, type);
    }
}
