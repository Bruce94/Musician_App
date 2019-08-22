package me.android.awm.musicianapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import me.android.awm.musicianapp.adapter.BandAdapter;
import me.android.awm.musicianapp.adapter.SkillsAdapter;
import me.android.awm.musicianapp.bean.BandBean;
import me.android.awm.musicianapp.bean.SkillInfoBean;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

@SuppressLint("ValidFragment")
public class BandFragment extends Fragment {
    public static String FRAGMENT_TAG = "BandFragment";

    private BandBean band;
    private CircleImageView pic_band;
    private TextView text_name, text_description, text_genre, text_n_candidates, text_n_members;
    private Button candidate_btn;
    private LinearLayout list_skills, candidates_btn, members_btn;
    private List<SkillInfoBean> skills = new LinkedList<SkillInfoBean>();
    private List<String> skills_selected = new LinkedList<String>();
    private boolean leader;

    @SuppressLint("ValidFragment")
    public BandFragment(BandBean band) {
        this.band = band;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_band, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        pic_band = getActivity().findViewById(R.id.pic_band);
        text_name = getActivity().findViewById(R.id.text_name);
        text_description = getActivity().findViewById(R.id.text_description);
        text_genre = getActivity().findViewById(R.id.text_genre);
        text_n_candidates = getActivity().findViewById(R.id.text_n_candidates);
        text_n_members = getActivity().findViewById(R.id.text_n_members);
        list_skills = getActivity().findViewById(R.id.list_skills);
        candidates_btn = getActivity().findViewById(R.id.candidates_btn);
        members_btn = getActivity().findViewById(R.id.members_btn);
        candidate_btn = getActivity().findViewById(R.id.candidate_btn);

        list_skills.removeAllViews();

        text_name.setText(band.getName_band());
        text_description.setText(band.getBio_band());
        text_genre.setText(band.getGenre_band());
        get_band_info();

        members_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                band_members(false);
            }
        });

        pic_band.setImageURI(Uri.fromFile(new File(band.getImg_band())));
    }

    static class ViewHolderSkill {
        public ImageView skill_img;
        public TextView num_need_skill;
    }
    private void setupNeedSkills(){
        for(int j = 0; j < band.getSkills().length(); j++){
            try {
                JSONObject s = band.getSkills().getJSONObject(j);
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v2 = inflater.inflate(R.layout.col_skill_need, null);
                ViewHolderSkill vhs = new ViewHolderSkill();
                SkillInfoBean skill = UserPrefHelper.getSkillBean(s.getString("name"));
                skills.add(skill);
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
                list_skills.addView(v2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void band_members(boolean candidates){
        MembersFragment fragment = new MembersFragment();
        Bundle arguments = new Bundle();
        arguments.putInt( "band_id" , band.getId());
        arguments.putBoolean("candidates", candidates);
        arguments.putBoolean("leader",leader);
        fragment.setArguments(arguments);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void get_band_info(){
        try {
            MusicianServerApi.getBandInfo(MainApplication.getInstance().getCurrentActivity(),
                    new HttpManager.HttpManagerCallback() {
                        @Override
                        public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                            JSONObject json = new JSONObject(response);
                            if(json.has("error"))
                                Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                        "Server error", Toast.LENGTH_SHORT).show();
                            else{
                                if(json.has("leader")){
                                    leader = json.getBoolean("leader");
                                    if(leader) {
                                        text_n_candidates.setText(json.getString("candidates")+" Candidates");
                                        candidates_btn.setVisibility(View.VISIBLE);
                                        candidates_btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                band_members(true);
                                            }
                                        });
                                    }
                                    else{
                                        candidates_btn.setVisibility(View.GONE);
                                        if(json.getInt("status") == 0){
                                            candidate_btn.setText("Candidate");
                                            candidate_btn.setVisibility(View.VISIBLE);
                                            candidate_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    show_alert_candidate();
                                                }
                                            });
                                        }
                                        else if(json.getInt("status") == 1){
                                            candidate_btn.setText("Remove Candidature");
                                            candidate_btn.setVisibility(View.VISIBLE);
                                            candidate_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    remove_candidature();
                                                }
                                            });
                                        }
                                    }
                                }
                                text_n_members.setText(json.getString("members")+" Members");
                                setupNeedSkills();
                            }
                        }
                    },band.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void show_alert_candidate(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_candidate);

        Button cancel_btn = dialog.findViewById(R.id.cancel_btn);
        Button confirm_btn = dialog.findViewById(R.id.confirm_btn);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ListView listView = dialog.findViewById(R.id.list_candidate_skills);
        SkillsAdapter arrayAdapter = new SkillsAdapter(getActivity(), skills);
        listView.setAdapter(arrayAdapter);

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (SkillInfoBean s : skills) {
                    if (s.getState()) {
                        skills_selected.add(s.getName());
                    }
                }

                if(skills_selected.size() > 0) {
                    send_candidature();
                    dialog.dismiss();
                }
                else
                    Toast.makeText(getActivity(),"Select at least one skill", Toast.LENGTH_SHORT);
            }
        });
        dialog.show();
    }

    private void send_candidature(){
        try {
            MusicianServerApi.userCandidateBand(getActivity(), new HttpManager.HttpManagerCallback() {
                @Override
                public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                    JSONObject json = new JSONObject(response);
                    if(json.has("error"))
                        Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                "Server error", Toast.LENGTH_SHORT).show();
                    else{
                        candidate_btn.setText("Remove Candidature");
                        candidate_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                remove_candidature();
                            }
                        });
                        String mess = band.getLeader_id() + "";
                        MainApplication.socket.emit("new general notification", mess);
                    }
                }
            },band.getId(),skills_selected);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void remove_candidature() {
        try {
            MusicianServerApi.userRemoveCandBand(getActivity(), new HttpManager.HttpManagerCallback() {
                @Override
                public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                    JSONObject json = new JSONObject(response);
                    if(json.has("error"))
                        Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                "Server error", Toast.LENGTH_SHORT).show();
                    else{
                        candidate_btn.setText("Candidate");
                        candidate_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                show_alert_candidate();
                            }
                        });
                    }
                }
            }, band.getId(),UserPrefHelper.getCurrentUser().getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
