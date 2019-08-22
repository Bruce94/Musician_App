package me.android.awm.musicianapp.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.android.awm.musicianapp.BandFragment;
import me.android.awm.musicianapp.MainApplication;
import me.android.awm.musicianapp.PortalActivity;
import me.android.awm.musicianapp.ProfileFragment;
import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.bean.BandBean;
import me.android.awm.musicianapp.bean.MemberBean;
import me.android.awm.musicianapp.bean.SkillInfoBean;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

public class MemberAdapter extends ArrayAdapter {
    private Context context;
    private List<MemberBean> members;
    private boolean leader;
    private List<SkillInfoBean> skills = new LinkedList<>();
    private List<String> skills_selected = new LinkedList<>();



    public MemberAdapter(Context context, List<MemberBean> members, boolean leader) {
        super(context, R.layout.row_member, members);
        this.context = context;
        this.members = members;
        this.leader = leader;
    }

    static class ViewHolder{
        public CircleImageView pic_member;
        public TextView name_member;
        public ImageView icon_leader;
        public ImageView icon_delete;
        public LinearLayout list_skills;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final MemberBean i = members.get(position);
        View v = null;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            v = layoutInflater.inflate(R.layout.row_member, null);
            viewHolder = new ViewHolder();
            viewHolder.pic_member = v.findViewById(R.id.pic_member);
            viewHolder.name_member = v.findViewById(R.id.name_member);
            viewHolder.icon_leader = v.findViewById(R.id.icon_leader);
            viewHolder.icon_delete = v.findViewById(R.id.icon_delete);
            viewHolder.list_skills = v.findViewById(R.id.list_skills);
            for(int j = 0; j < i.getSkills().length(); j++){
                try {
                    JSONObject s = i.getSkills().getJSONObject(j);
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v2 = inflater.inflate(R.layout.col_skill_need, null);
                    BandAdapter.ViewHolderSkill vhs = new BandAdapter.ViewHolderSkill();
                    SkillInfoBean skill = UserPrefHelper.getSkillBean(s.getString("name"));
                    vhs.skill_img = v2.findViewById(R.id.skill_elem_img);
                    vhs.num_need_skill = v2.findViewById(R.id.num_need_skill);
                    vhs.skill_img.setImageResource(skill.getIcon_code());
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
        viewHolder.pic_member.setImageURI(Uri.fromFile(new File(i.getUser().getImg())));
        viewHolder.name_member.setText(i.getUser().getFname()+" "+i.getUser().getLname());

        if(i.isLeader())
            viewHolder.icon_leader.setVisibility(View.VISIBLE);
        else
            viewHolder.icon_leader.setVisibility(View.GONE);

        try {
            if(leader && i.getStatus()==2
                    && UserPrefHelper.getCurrentUser().getId() != i.getUser().getId()) {
                viewHolder.icon_delete.setVisibility(View.VISIBLE);
                viewHolder.icon_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remove_member(i);
                    }
                });
            }
            else
                viewHolder.icon_delete.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i.getStatus()==2) {
                    go_to_profile(i);
                }
                else{
                    show_alert_accept_candidate(i);
                }
            }
        });
        return v;
    }

    private void remove_member(final MemberBean m){
        try {
            MusicianServerApi.userRemoveCandBand(context, new HttpManager.HttpManagerCallback() {
                @Override
                public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                    JSONObject json = new JSONObject(response);
                    if(json.has("error"))
                        Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                "Server error", Toast.LENGTH_SHORT).show();
                    else{
                        members.remove(m);
                        notifyDataSetChanged();
                    }
                }
            }, m.getBand().getId(),m.getUser().getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void go_to_profile(MemberBean m){
        ProfileFragment bandFragment = new ProfileFragment(m.getUser());

        FragmentTransaction fragmentTransaction = ((PortalActivity) MainApplication.getInstance().
                getCurrentActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, bandFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void accept_candidature(final MemberBean m){
        try {
            MusicianServerApi.userAcceptCandBand(context, new HttpManager.HttpManagerCallback() {
                @Override
                public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                    JSONObject json = new JSONObject(response);
                    if(json.has("error"))
                        Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                "Server error", Toast.LENGTH_SHORT).show();
                    else{
                        members.remove(m);
                        notifyDataSetChanged();
                    }
                }
            }, m.getBand().getId(),m.getUser().getId(),skills_selected);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void show_alert_accept_candidate(final MemberBean m){
        final Dialog dialog = new Dialog(context);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_accept_candidate);

        ImageView cancel_btn = dialog.findViewById(R.id.cancel_btn);
        Button accept_btn = dialog.findViewById(R.id.accept_btn);
        Button profile_btn = dialog.findViewById(R.id.profile_btn);
        Button refuse_btn = dialog.findViewById(R.id.refuse_btn);

        skills.clear();
        skills_selected.clear();
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ListView listView = dialog.findViewById(R.id.list_candidate_skills);
        for(int i = 0; i < m.getSkills().length(); i++){
            try {
                SkillInfoBean skill = UserPrefHelper.getSkillBean(m.getSkills().getJSONObject(i).getString("name"));
                skills.add(skill);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        SkillsAdapter arrayAdapter = new SkillsAdapter(context, skills);
        listView.setAdapter(arrayAdapter);


        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(skills.size() > 0) {
                    for (SkillInfoBean s : skills) {
                        if (s.getState()) {
                            skills_selected.add(s.getName());
                        }
                    }
                    accept_candidature(m);
                    dialog.dismiss();
                }
            }
        });

        refuse_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove_member(m);
                dialog.dismiss();

            }
        });

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_to_profile(m);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
