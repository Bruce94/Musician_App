package me.android.awm.musicianapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.android.awm.musicianapp.BandFragment;
import me.android.awm.musicianapp.MainApplication;
import me.android.awm.musicianapp.PortalActivity;
import me.android.awm.musicianapp.ProfileFragment;
import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.bean.BandBean;
import me.android.awm.musicianapp.bean.SkillInfoBean;
import me.android.awm.musicianapp.utils.UserPrefHelper;

public class BandAdapter extends ArrayAdapter {
    private Context context;
    private List<BandBean> bands;

    public BandAdapter(Context context, List<BandBean> bands) {
        super(context, R.layout.row_band, bands);
        this.context = context;
        this.bands = bands;
    }

    static class ViewHolder{
        public CircleImageView pic_band;
        public TextView name_band;
        public LinearLayout list_skills;
    }

    static class ViewHolderSkill{
        public ImageView skill_img;
        public TextView num_need_skill;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final BandBean i = bands.get(position);
        View v = null;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            v = layoutInflater.inflate(R.layout.row_band, null);
            viewHolder = new ViewHolder();
            viewHolder.pic_band = v.findViewById(R.id.pic_band);
            viewHolder.name_band = v.findViewById(R.id.name_band);
            viewHolder.list_skills = v.findViewById(R.id.list_skills);
            for(int j = 0; j < i.getSkills().length(); j++){
                try {
                    JSONObject s = i.getSkills().getJSONObject(j);
                    System.out.println(i.getName_band()+ " "+s.getString("name"));
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v2 = inflater.inflate(R.layout.col_skill_need, null);
                    ViewHolderSkill vhs = new ViewHolderSkill();
                    SkillInfoBean skill = UserPrefHelper.getSkillBean(s.getString("name"));
                    vhs.skill_img = v2.findViewById(R.id.skill_elem_img);
                    vhs.num_need_skill = v2.findViewById(R.id.num_need_skill);
                    vhs.skill_img.setImageResource(skill.getIcon_code());
                    if(s.getInt("number") > 1){
                        vhs.num_need_skill.setVisibility(View.VISIBLE);
                        vhs.num_need_skill.setText(s.getString("number"));
                    }
                    else
                        vhs.num_need_skill.setVisibility(View.GONE);
                    v2.setTag(vhs);
                    viewHolder.list_skills.addView(v2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            v.setTag(viewHolder);
        }
        else{
            v = convertView;
            viewHolder = (ViewHolder) v.getTag();
        }
        viewHolder.pic_band.setImageURI(Uri.fromFile(new File(i.getImg_band())));
        viewHolder.name_band.setText(i.getName_band());

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BandFragment bandFragment = new BandFragment(i);

                FragmentTransaction fragmentTransaction = ((PortalActivity) MainApplication.getInstance().
                        getCurrentActivity()).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,bandFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return v;
    }
}
