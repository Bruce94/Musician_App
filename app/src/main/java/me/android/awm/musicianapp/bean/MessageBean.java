package me.android.awm.musicianapp.bean;

import org.json.JSONObject;

public class MessageBean {
    private String text;
    private String date;
    private boolean sender;

    public MessageBean(String json) throws Exception {
        JSONObject jsonObject = new JSONObject(json);
        text = jsonObject.getString("text");
        sender = jsonObject.getBoolean("sender");
        if(jsonObject.has("date"))
            date = jsonObject.getString("date");
        else
            date = "";
    }

    public String getText() {
        return text;
    }

    public void setText(String message) {
        this.text = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSender() {
        return sender;
    }

    public void setSender(boolean sender) {
        this.sender = sender;
    }
}
