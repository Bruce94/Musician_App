package me.android.awm.musicianapp.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class PostBean {
    private int id;
    private int musician_id;
    private String username;
    private String post_text;
    private String musician_fname;
    private String musician_lname;
    private String pub_date;
    private int n_like;
    private int n_dislike;
    private int n_comments;
    private int vote;
    private String musician_img;
    private String post_image;

    public PostBean(String json) throws Exception {
        JSONObject jsonObject = new JSONObject(json);
        id = jsonObject.getInt("id");
        musician_id = jsonObject.getInt("musician_id");
        musician_fname = jsonObject.getString("musician_fname");
        musician_lname = jsonObject.getString("musician_lname");
        post_text = jsonObject.getString("post_text");
        pub_date = jsonObject.getString("pub_date");
        username = jsonObject.getString("username");
        if(jsonObject.has("post_image")){
            post_image = jsonObject.getString("post_image");
        }
        else{
            post_image = "";
        }
        if(jsonObject.has("musician_img")){
            musician_img = jsonObject.getString("musician_img");
        }
        else{
            musician_img = "";
        }
        if(jsonObject.has("n_like")){
            n_like = jsonObject.getInt("n_like");
        }
        else{
            n_like = -1;
        }
        if(jsonObject.has("n_dislike")){
            n_dislike = jsonObject.getInt("n_dislike");
        }
        else{
            n_dislike = -1;
        }
        if(jsonObject.has("n_comments")){
            n_comments = jsonObject.getInt("n_comments");
        }
        else{
            n_comments = -1;
        }
        if(jsonObject.has("voted")){
            vote = jsonObject.getInt("voted");
        }
        else{
            vote = 0;
        }
    }

    public String getPostBeanJsonString() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("musician_id", musician_id);
        jsonObject.put("username", username);
        jsonObject.put("post_text", post_text);
        jsonObject.put("musician_fname",musician_fname);
        jsonObject.put("musician_lname", musician_lname);
        jsonObject.put("pub_date", pub_date);
        jsonObject.put("n_like", n_like);
        jsonObject.put("n_dislike", n_dislike);
        jsonObject.put("n_comments", n_comments);
        jsonObject.put("vote", vote);
        jsonObject.put("musician_img", musician_img);
        jsonObject.put("post_image", post_image);

        return jsonObject.toString();
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

    public String getPost_text() {
        return post_text;
    }

    public void setPost_text(String post_text) {
        this.post_text = post_text;
    }

    public String getMusician_fname() {
        return musician_fname;
    }

    public void setMusician_fname(String musician_fname) {
        this.musician_fname = musician_fname;
    }

    public String getMusician_lname() {
        return musician_lname;
    }

    public void setMusician_lname(String musician_lname) {
        this.musician_lname = musician_lname;
    }

    public String getPub_date() {
        return pub_date;
    }

    public void setPub_date(String pub_date) {
        this.pub_date = pub_date;
    }

    public int getN_like() {
        return n_like;
    }

    public void setN_like(int n_like) {
        this.n_like = n_like;
    }

    public int getN_dislike() {
        return n_dislike;
    }

    public void setN_dislike(int n_dislike) {
        this.n_dislike = n_dislike;
    }

    public int getN_comments() {
        return n_comments;
    }

    public void setN_comments(int n_comments) {
        this.n_comments = n_comments;
    }

    public String getMusician_img() {
        return musician_img;
    }

    public void setMusician_img(String musician_img) {
        this.musician_img = musician_img;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }
}
