package me.android.awm.musicianapp.bean;

import org.json.JSONArray;
import org.json.JSONObject;

public class MemberBean {
    private UserBean user;
    private boolean leader;
    private int status;
    private JSONArray skills;
    private BandBean band;

    public MemberBean(String json) throws Exception {
        JSONObject jsonObject = new JSONObject(json);
        user = new UserBean(jsonObject.getJSONObject("user").toString());
        band = new BandBean(jsonObject.getJSONObject("band").toString());
        leader = jsonObject.getBoolean("leader");
        status = jsonObject.getInt("status");
        if(jsonObject.has("skills")){
            skills = jsonObject.getJSONArray("skills");
        }
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public BandBean getBand() {
        return band;
    }

    public void setBand(BandBean band) {
        this.band = band;
    }

    public boolean isLeader() {
        return leader;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public JSONArray getSkills() {
        return skills;
    }

    public void setSkills(JSONArray skills) {
        this.skills = skills;
    }
}
