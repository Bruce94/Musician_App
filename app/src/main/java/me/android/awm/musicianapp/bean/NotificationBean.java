package me.android.awm.musicianapp.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationBean {
    private int id;
    private String type;
    private String fname;
    private String lname;
    private String username;
    private String date;
    private String vote;
    private PostBean post;
    private BandBean band;
    private String img;

    public NotificationBean(String json) throws Exception {
        JSONObject jsonObject = new JSONObject( json);
        id = jsonObject.getInt("id");
        type = jsonObject.getString("type");
        fname = jsonObject.getString("fname");
        lname = jsonObject.getString("lname");
        date = jsonObject.getString("date");
        username = jsonObject.getString("username");
        if(jsonObject.has("img")){
            img = jsonObject.getString("img");
        }
        else{
            img = "";
        }
        if(jsonObject.has("post")){
            post = new PostBean(jsonObject.getJSONObject("post").toString());
        }
        else{
            post = null;
        }
        if(jsonObject.has("band")){
            band = new BandBean(jsonObject.getJSONObject("band").toString());
        }
        else{
            band = null;
        }
        if(jsonObject.has("vote")){
            vote = jsonObject.getString("vote");
        }
        else{
            vote = "";
        }

    }
    public String getNotificationBeanJsonString()throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("type", type);
        jsonObject.put("fname", fname);
        jsonObject.put("lname", lname);
        jsonObject.put("img", img);
        jsonObject.put("post", post);
        jsonObject.put("band", band);
        jsonObject.put("date", date);
        jsonObject.put("vote", vote);
        jsonObject.put("username",username);


        return jsonObject.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public PostBean getPost() {
        return post;
    }

    public void setPost(PostBean post) {
        this.post = post;
    }

    public BandBean getBand() {
        return band;
    }

    public void setBand(BandBean band) {
        this.band = band;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
