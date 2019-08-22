package me.android.awm.musicianapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import me.android.awm.musicianapp.MainApplication;
import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.bean.SkillInfoBean;
import me.android.awm.musicianapp.bean.UserBean;

import static android.content.Context.MODE_PRIVATE;

public class UserPrefHelper {

    public static void setUserPersData(String fname, String lname, String bday, String gender){
        SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(
                MainApplication.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("fname",fname);
        editor.putString("lname",lname);
        editor.putString("bday",bday);
        editor.putString("gender",gender);
        editor.commit();
    }

    public static void setUserAuthData(String username, String pwd, String email){
        SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(
                MainApplication.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username",username);
        editor.putString("pwd",pwd);
        editor.putString("email",email);
        editor.commit();
    }

    public static void setUserGeneralData(String phone, String country, String city){
        SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(
                MainApplication.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("phone",phone);
        editor.putString("country",country);
        editor.putString("city",city);
        editor.commit();
    }

    public static void setUserBioData(String bio){
        SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(
                MainApplication.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("bio",bio);
        editor.commit();
    }

    public static void setUserPhotoData(String imageUri){
        SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(
                MainApplication.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("imageUri", imageUri);
        editor.commit();
    }

    public static void setUserSkillsData(List<String> skills_selected){
        SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(
                MainApplication.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String skills = "";
        for(String s:skills_selected){
            skills = skills+s+",";
        }
        editor.putString("skills",skills);
        editor.commit();
    }

    public static JSONObject getUserData(){
        JSONObject json = new JSONObject();
        SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(MainApplication.PREFERENCES,Context.MODE_PRIVATE);
        try {
            json.put("fname",sp.getString("fname", ""));
            json.put("lname",sp.getString("lname", ""));
            json.put("date",sp.getString("bday", ""));
            json.put("gender",sp.getString("gender", ""));
            json.put("username",sp.getString("username", ""));
            json.put("pwd",sp.getString("pwd", ""));
            json.put("email",sp.getString("email", ""));
            json.put("phone_number",sp.getString("phone", ""));
            json.put("country",sp.getString("country", ""));
            json.put("city",sp.getString("city", ""));
            json.put("bio",sp.getString("bio", ""));
            json.put("img",sp.getString("imageUri", ""));
            json.put("skills",sp.getString("skills", ""));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static void clearUserData(){
        SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(
                MainApplication.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("fname","");
        editor.putString("lname","");
        editor.putString("bday","");
        editor.putString("gender","");
        editor.putString("username","");
        editor.putString("pwd","");
        editor.putString("email","");
        editor.putString("phone","");
        editor.putString("country","");
        editor.putString("city","");
        editor.putString("bio","");
        editor.putString("imageUri","");
        editor.putString("skills","");

        editor.commit();
    }

    public static void setLoggedUser(String userJson){
        SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(
                MainApplication.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("LOGGED_USER",userJson);
        editor.commit();
    }

    public static String getLoggedUser(){
        String userJson;
        SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(
                MainApplication.PREFERENCES,Context.MODE_PRIVATE);
        userJson = sp.getString("LOGGED_USER", null);
        return userJson;
    }

    public static void userLogOut(){
        SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(
                MainApplication.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("LOGGED_USER",null);
        editor.commit();
    }

    public static UserBean getCurrentUser() throws Exception {
        String json = UserPrefHelper.getLoggedUser();
        System.out.println(json);
        UserBean userBean = null;
        if (json != null) {
            userBean = new UserBean(json);
        }
        return userBean;
    }

    public static boolean checkIfUserIsLogged() throws Exception {
        return getCurrentUser() != null;
    }

    public static JSONObject getLoggedUsername() throws Exception{
        JSONObject juser = new JSONObject(UserPrefHelper.getLoggedUser());
        JSONObject jusername = new JSONObject();
        jusername.accumulate("id", juser.get("id").toString());
        return jusername;
    }

    public static SkillInfoBean getSkillBean(String skill){
        switch (skill){
            case "Acoustic Guitar":
                return new SkillInfoBean("Acoustic Guitar", R.drawable.acoustic_guitar_icon);
            case "Bass Guitar":
                return new SkillInfoBean("Bass Guitar", R.drawable.bass_guitar_icon);
            case "DJ":
                return new SkillInfoBean("DJ", R.drawable.dj_icon);
            case "Drum":
                return new SkillInfoBean("Drum", R.drawable.drum_set_icon);
            case "Electric Guitar":
                return new SkillInfoBean("Electric Guitar", R.drawable.guitar_icon);
            case "Harp":
                return new SkillInfoBean("Harp", R.drawable.harp_icon);
            case "Percussion":
                return new SkillInfoBean("Percussion", R.drawable.conga_icon);
            case "Piano":
                return new SkillInfoBean("Piano", R.drawable.piano_icon);
            case "Saxophone":
                return new SkillInfoBean("Saxophone", R.drawable.saxophone_icon);
            case "Violin":
                return new SkillInfoBean("Violin", R.drawable.violin_icon);
            case "Voice":
                return new SkillInfoBean("Voice", R.drawable.microphone_icon);
            default:
                return new SkillInfoBean("default", R.drawable.vm);

        }
    }
}

