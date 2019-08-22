package me.android.awm.musicianapp.bean;

import org.json.JSONArray;
import org.json.JSONObject;

public class BandBean {
    private int id;
    private int leader_id;
    private String name_band;
    private String genre_band;
    private String bio_band;
    private JSONArray skills;
    private String img_band;

    public BandBean(String json) throws Exception {
        JSONObject jsonObject = new JSONObject( json);
        id = jsonObject.getInt("id");
        name_band = jsonObject.getString("name_band");
        if (jsonObject.has("genre_band"))
            genre_band = jsonObject.getString("genre_band");
        else
            genre_band = "";
        if (jsonObject.has("bio_band"))
            bio_band = jsonObject.getString("bio_band");
        else
            bio_band = "";
        if (jsonObject.has("leader_id"))
            leader_id = jsonObject.getInt("leader_id");
        else
            leader_id = -1;
        if(jsonObject.has("img_band"))
            img_band = jsonObject.getString("img_band");
        if(jsonObject.has("skills"))
            skills = jsonObject.getJSONArray("skills");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName_band() {
        return name_band;
    }

    public void setName_band(String name_band) {
        this.name_band = name_band;
    }

    public String getGenre_band() {
        return genre_band;
    }

    public void setGenre_band(String genre_band) {
        this.genre_band = genre_band;
    }

    public String getBio_band() {
        return bio_band;
    }

    public void setBio_band(String bio_band) {
        this.bio_band = bio_band;
    }

    public JSONArray getSkills() {
        return skills;
    }

    public void setSkills(JSONArray skills) {
        this.skills = skills;
    }

    public String getImg_band() {
        return img_band;
    }

    public void setImg_band(String img_band) {
        this.img_band = img_band;
    }

    public int getLeader_id() {
        return leader_id;
    }

    public void setLeader_id(int leader_id) {
        this.leader_id = leader_id;
    }
}
