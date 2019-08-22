package me.android.awm.musicianapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.bean.SkillInfoBean;

public class SkillsAdapter extends ArrayAdapter implements CompoundButton.OnCheckedChangeListener {
    private Context context;
    private List<SkillInfoBean> skills;

    public SkillsAdapter(Context context, List<SkillInfoBean> skills) {
        super(context, R.layout.row_skill, skills);
        this.context = context;
        this.skills = skills;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        for(SkillInfoBean s : skills){
            if(s.getName().equals(buttonView.getTag().toString())){
                s.setState(isChecked);
            }
        }
    }

    static class ViewHolder{
        public ImageView skill_elem_img;
        public TextView name_skill;
        public CheckBox skill_check;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        SkillInfoBean i = skills.get(position);
        View v = null;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            v = layoutInflater.inflate(R.layout.row_skill, null);
            viewHolder = new ViewHolder();
            viewHolder.skill_elem_img = (ImageView) v.findViewById(R.id.skill_elem_img);
            viewHolder.name_skill = (TextView) v.findViewById(R.id.name_skill);
            viewHolder.skill_check = (CheckBox) v.findViewById(R.id.skill_check);
            v.setTag(viewHolder);
        }
        else{
            v = convertView;
            viewHolder = (ViewHolder) v.getTag();
        }
        viewHolder.skill_elem_img.setImageResource(i.getIcon_code());
        viewHolder.name_skill.setText(i.getName());
        viewHolder.skill_check.setTag(i.getName());
        viewHolder.skill_check.setChecked(i.getState());
        viewHolder.skill_check.setOnCheckedChangeListener(this);
        return v;
    }
}
