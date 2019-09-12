package me.android.awm.musicianapp.bean;

import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.utils.UserPrefHelper;

public class UserBean {
    private int id;
    private String email;
    private String pwd;
    private String username;
    private String fname;
    private String lname;
    private String gender;
    private String bday;
    private String bio;
    private String phone;
    private String city;
    private String country;
    private JSONArray skills;
    private String img;
    private String server_img;

    public UserBean(int id, String email, String pwd, String username, String fname, String lname,
                    String gender, String bio, String phone, String city, String country,
                    JSONArray skills, String img, String bday){
        this.id = id;
        this.email = email;
        this.pwd = pwd;
        this.username = username;
        this.fname = fname;
        this.lname = lname;
        this.gender = gender;
        this.bio = bio;
        this.phone = phone;
        this.city = city;
        this.country = country;
        this.skills = skills;
        this.img = img;
        this.bday = bday;
        this.server_img = "";
    }

    public UserBean(String json) throws Exception {
        JSONObject jsonObject = new JSONObject( json);
        id = jsonObject.getInt("id");
        email = jsonObject.getString("email");
        if(jsonObject.has("pwd")){
            pwd = jsonObject.getString("pwd");
        }
        else{
            pwd = "";
        }
        username = jsonObject.getString("username");
        fname = jsonObject.getString("fname");;
        lname = jsonObject.getString("lname");;
        gender = jsonObject.getString("gender");;
        bio = jsonObject.getString("bio");;
        phone = jsonObject.getString("phone");;
        city = jsonObject.getString("city");;
        country = jsonObject.getString("country");
        if(jsonObject.has("img")){
            img = jsonObject.getString("img");
        }
        if(jsonObject.has("skills")){
            skills = jsonObject.getJSONArray("skills");
        }
        bday = jsonObject.getString("bday");
        if(jsonObject.has("server_img"))
            server_img = jsonObject.getString("server_img");
        else
            server_img = "";
    }

    public String getUserBeanJsonString()throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("email", email);
        jsonObject.put("pwd", pwd);
        jsonObject.put("username", username);
        jsonObject.put("fname",fname);
        jsonObject.put("lname", lname);
        jsonObject.put("gender", gender);
        jsonObject.put("bio", bio);
        jsonObject.put("phone", phone);
        jsonObject.put("city", city);
        jsonObject.put("country", country);
        jsonObject.put("img", img);
        jsonObject.put("bday", bday);
        jsonObject.put("skills", skills);
        jsonObject.put("server_img", server_img);

        return jsonObject.toString();
    }

    public void setId(int id){
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setSkills(JSONArray skills) {
        this.skills = skills;
    }

    public void setBday(String bday){
        this.bday = bday;
    }

    public int getId(){
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPwd() {
        return pwd;
    }

    public String getUsername() {
        return username;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getGender() {
        return gender;
    }

    public String getBio() {
        return bio;
    }

    public String getPhone() {
        return phone;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public JSONArray getSkills() {
        return skills;
    }

    public String getImg() {
        return img;
    }

    public String getBday(){
        return bday;
    }

    public String getServer_img() {
        return server_img;
    }

    public void setServer_img(String server_img) {
        this.server_img = server_img;
    }

    public void setUserInfoData(String gender, String city, String email, String phone){
        this.gender = gender;
        this.city = city;
        this.email = email;
        this.phone = phone;
    }

    public void setSkillsWithName(List<String> skills_strings) {
        JSONArray jsonArray_skills = new JSONArray();
        for (String cs: skills_strings) {
            jsonArray_skills.put(cs);
        }
       // jsonObject.put("new_skills", jsonArray);

        this.skills = jsonArray_skills;
    }
}
