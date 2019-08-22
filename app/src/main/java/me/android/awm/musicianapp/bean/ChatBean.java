package me.android.awm.musicianapp.bean;

import org.json.JSONObject;

public class ChatBean {
    private int user_id;
    private String fname;
    private String lname;
    private String username;
    private String message;
    private String date;
    private String musician_img;
    private boolean seen;

    public ChatBean(String json) throws Exception {
        JSONObject jsonObject = new JSONObject(json);
        user_id = jsonObject.getInt("user_id");
        fname = jsonObject.getString("fname");
        lname = jsonObject.getString("lname");
        message = jsonObject.getString("message");
        username = jsonObject.getString("username");
        if (jsonObject.has("seen"))
            seen = jsonObject.getBoolean("seen");
        else
            seen = true;
        if(jsonObject.has("date"))
            date = jsonObject.getString("date");
        else
            date = "";
        fname = jsonObject.getString("fname");
        if(jsonObject.has("img"))
            musician_img = jsonObject.getString("img");
        else{
            musician_img = "";
        }
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMusician_img() {
        return musician_img;
    }

    public void setMusician_img(String musician_img) {
        this.musician_img = musician_img;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
