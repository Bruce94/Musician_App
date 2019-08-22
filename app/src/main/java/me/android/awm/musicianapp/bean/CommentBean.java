package me.android.awm.musicianapp.bean;

import org.json.JSONObject;

public class CommentBean {
    private int id;
    private int musician_id;
    private String username;
    private String comment_text;
    private String pub_date;
    private String musician_img;

    public CommentBean(String json) throws Exception {
        JSONObject jsonObject = new JSONObject(json);
        id = jsonObject.getInt("id");
        musician_id = jsonObject.getInt("musician_id");
        comment_text = jsonObject.getString("comment_text");
        pub_date = jsonObject.getString("pub_date");
        username = jsonObject.getString("username");
        if(jsonObject.has("musician_img")){
            musician_img = jsonObject.getString("musician_img");
        }
        else{
            musician_img = "";
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMusician_id() {
        return musician_id;
    }

    public void setMusician_id(int musician_id) {
        this.musician_id = musician_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getPub_date() {
        return pub_date;
    }

    public void setPub_date(String pub_date) {
        this.pub_date = pub_date;
    }

    public String getMusician_img() {
        return musician_img;
    }

    public void setMusician_img(String musician_img) {
        this.musician_img = musician_img;
    }
}
