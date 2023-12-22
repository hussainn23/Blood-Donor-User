package com.khushi.blooddonors;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.khushi.blooddonors.Models.ModelUser;

public class SharedPrefManager {

    private final SharedPreferences sharedPref;
    private final SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        sharedPref = context.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPref.getBoolean("isLoggedIn", false);
    }

    public void saveDonor(ModelUser user) {
        if (user != null) {
            editor.putString("user", new Gson().toJson(user));
        } else {
            editor.remove("user");
        }
        editor.apply();
    }

    public void putToken(String token) {
        editor.putString("userToken", token);
        editor.apply();
    }

    public String getToken() {
        return sharedPref.getString("userToken", "");
    }

    public ModelUser getUser() {
        String jsonString = sharedPref.getString("user", null);
        return jsonString != null ? new Gson().fromJson(jsonString, ModelUser.class) : null;
    }
    public void clearPreferences() {
        editor.remove("isLoggedIn");
        editor.remove("user");
        editor.remove("userToken");
        editor.apply();
    }


}
