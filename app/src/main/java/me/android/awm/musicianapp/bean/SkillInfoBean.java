package me.android.awm.musicianapp.bean;

public class SkillInfoBean {
    private String name;
    private int icon_code;
    private boolean state;

    public SkillInfoBean(String name, int icon_code){
        this.name = name;
        this.icon_code = icon_code;
        this.state = false;
    }

    public String getName(){
        return name;
    }

    public int getIcon_code(){
        return icon_code;
    }

    public boolean getState(){
        return state;
    }

    public void setState(boolean state){
        this.state = state;
    }
}
