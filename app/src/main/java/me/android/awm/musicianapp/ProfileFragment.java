package me.android.awm.musicianapp;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.android.awm.musicianapp.adapter.SkillsAdapter;
import me.android.awm.musicianapp.bean.BandBean;
import me.android.awm.musicianapp.bean.SkillInfoBean;
import me.android.awm.musicianapp.bean.UserBean;
import me.android.awm.musicianapp.net.DownloadTask;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ProfileFragment extends Fragment {
    public static String FRAGMENT_TAG = "ProfileFragment";

    private UserBean user;
    private CircleImageView pic_profile_user;
    private TextView text_name, text_bio, text_birthday, text_gender, text_country, text_city;
    private TextView text_email, text_phone, text_n_friends, text_n_posts, text_distance;
    private Button friendship_btn, send_message_btn;
    private LinearLayout list_skills, friend_btn, posts_btn, layout_band, list_band;
    private int friends_data, post_data;
    private int friendship = -1;
    private List<BandBean> bands = new LinkedList<>();
    private List<SkillInfoBean> skills = new LinkedList<SkillInfoBean>();
    private List<String> skills_selected = new LinkedList<String>();
    //private List<ModBioBean> bio = new LinkedList<ModBioBean>();
    private ImageView modify_bio_btn, modify_info_btn, modify_skills_btn;

    @SuppressLint("ValidFragment")
    public ProfileFragment(UserBean user) {
        // Required empty public constructor
        this.user = user;//new UserBean(UserPrefHelper.getLoggedUser());
        System.out.println("user GENDER: " + this.user.getGender());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        pic_profile_user = getActivity().findViewById(R.id.pic_profile_user);
        text_name = getActivity().findViewById(R.id.text_name);
        text_bio = getActivity().findViewById(R.id.text_bio);
        text_birthday = getActivity().findViewById(R.id.text_birthday);
        text_gender = getActivity().findViewById(R.id.text_gender);
        text_country = getActivity().findViewById(R.id.text_country);
        text_city = getActivity().findViewById(R.id.text_city);
        text_email = getActivity().findViewById(R.id.text_email);
        text_phone = getActivity().findViewById(R.id.text_phone);
        text_n_friends = getActivity().findViewById(R.id.text_n_friends);
        text_n_posts = getActivity().findViewById(R.id.text_n_posts);
        list_skills = getActivity().findViewById(R.id.list_skills);
        friend_btn = getActivity().findViewById(R.id.friend_btn);
        posts_btn = getActivity().findViewById(R.id.posts_btn);
        friendship_btn = getActivity().findViewById(R.id.friendship_btn);
        send_message_btn = getActivity().findViewById(R.id.send_message_btn);
        text_distance = getActivity().findViewById(R.id.text_distance);
        layout_band = getActivity().findViewById(R.id.layout_band);
        list_band = getActivity().findViewById(R.id.list_band);
        modify_bio_btn = getActivity().findViewById(R.id.modify_bio_btn);
        modify_info_btn = getActivity().findViewById(R.id.modify_info_btn);
        modify_skills_btn = getActivity().findViewById(R.id.modify_skills_btn);

        list_skills.removeAllViews();

        try {
            MusicianServerApi.getMusician(getActivity(), new HttpManager.HttpManagerCallback() {
                @Override
                public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                    JSONObject json = new JSONObject(response);
                    if(json.has("error")){
                        Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                "Server error", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONArray all_skills = json.getJSONArray("skills");
                    user.setSkills(all_skills);

                }
            },user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }


        text_name.setText(user.getFname() + " " +user.getLname());
        text_bio.setText(user.getBio());
        text_birthday.setText(user.getBday());
        text_gender.setText(user.getGender());
        text_country.setText(user.getCountry());
        text_city.setText(user.getCity());
        text_email.setText(user.getEmail());
        text_phone.setText(user.getPhone());
        get_user_info();
        fillSkillList();

        text_n_friends.setText(friends_data+" friends");
        text_n_posts.setText(post_data+" posts");

        pic_profile_user.setImageURI(Uri.fromFile(new File(user.getImg())));


        modify_bio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_mod_bio();
            }
        });
        modify_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_mod_info();
            }
        });
        modify_skills_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    user_mod_skills();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        friend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_friends();
            }
        });
        posts_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_posts();
            }
        });

        try {
            if(user.getId() == UserPrefHelper.getCurrentUser().getId()){
                friendship_btn.setVisibility(View.GONE);
                send_message_btn.setVisibility(View.GONE);
                text_distance.setVisibility(View.GONE);
            }
            else{
                modify_bio_btn.setVisibility(View.GONE);
                modify_info_btn.setVisibility(View.GONE);
                modify_skills_btn.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void fillSkillList(){
        skills.clear();
        skills.add(new SkillInfoBean("Acoustic Guitar", R.drawable.acoustic_guitar_icon));
        skills.add(new SkillInfoBean("Bass Guitar", R.drawable.bass_guitar_icon));
        skills.add(new SkillInfoBean("DJ", R.drawable.dj_icon));
        skills.add(new SkillInfoBean("Drum", R.drawable.drum_set_icon));
        skills.add(new SkillInfoBean("Electric Guitar", R.drawable.guitar_icon));
        skills.add(new SkillInfoBean("Harp", R.drawable.harp_icon));
        skills.add(new SkillInfoBean("Percussion", R.drawable.conga_icon));
        skills.add(new SkillInfoBean("Piano", R.drawable.piano_icon));
        skills.add(new SkillInfoBean("Saxophone", R.drawable.saxophone_icon));
        skills.add(new SkillInfoBean("Violin", R.drawable.violin_icon));
        skills.add(new SkillInfoBean("Voice", R.drawable.microphone_icon));
    }

    private void user_mod_skills() throws JSONException {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.alert_mod_skills);

        Button cancel_btn = dialog.findViewById(R.id.cancel_btn);
        Button confirm_btn = dialog.findViewById(R.id.confirm_btn);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        for(SkillInfoBean skill : skills) { skill.setState(false); }

        for(int j = 0; j < user.getSkills().length(); j++) {
            JSONObject s = (JSONObject) user.getSkills().get(j);
            SkillInfoBean old_skill = UserPrefHelper.getSkillBean(s.getString("name"));

            for(SkillInfoBean skill : skills)
                if(skill.getName().equals(old_skill.getName()))
                    skill.setState(true);
        }

        ListView listView = dialog.findViewById(R.id.list_mod_skills);
        SkillsAdapter arrayAdapter = new SkillsAdapter(getActivity(), skills);
        listView.setAdapter(arrayAdapter);

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skills_selected.clear();
                for (SkillInfoBean s : skills)
                    if (s.getState())
                        skills_selected.add(s.getName());

                try {
                    MusicianServerApi.changeSkills(MainApplication.getInstance().getCurrentActivity(),
                            new HttpManager.HttpManagerCallback() {
                                @Override
                                public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                                    JSONObject json = new JSONObject(response);
                                    if(json.has("error")) {
                                        Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                                "Server error", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(json.has("message")){
                                        if(skills_selected.size() > 0) {
                                            //JSONArray endorsers = s.getJSONArray("endorsers");
                                            list_skills.removeAllViews();
                                            JSONArray new_skills = json.getJSONArray("new_skills");
                                            user.setSkills(new_skills);
                                            UserPrefHelper.setLoggedUser(user.getUserBeanJsonString());
                                            setupUserSkills();

                                            Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                                    "Skills updated successfully", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                        else
                                            Toast.makeText(getActivity(),"Select at least one skill", Toast.LENGTH_SHORT);

                                    }
                                }
                            },skills_selected);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }

    private void user_mod_info(){
        final Dialog dialog = new Dialog(getActivity());

        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_mod_info);

        final EditText new_email = dialog.findViewById(R.id.edit_reg_email);
        final EditText new_phone = dialog.findViewById(R.id.edit_reg_phone);
        final EditText new_city = dialog.findViewById(R.id.edit_reg_city);
        final RadioButton radio_male = dialog.findViewById(R.id.radio_male);
        final RadioButton radio_female = dialog.findViewById(R.id.radio_female);
        //mbio.setText(user.getBio());
        Button cancel_btn = dialog.findViewById(R.id.cancel_btn);
        Button confirm_btn = dialog.findViewById(R.id.confirm_btn);

        if(text_gender.getText().toString().equals("M")){
            radio_male.setChecked(true);
        }
        else if(text_gender.getText().toString().equals("F")){
            radio_female.setChecked(true);
        }
        new_email.setText(user.getEmail());
        new_phone.setText(user.getPhone());
        new_city.setText(user.getCity());

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!new_email.getText().toString().isEmpty() && !new_phone.getText().toString().isEmpty() && !new_city.getText().toString().isEmpty() ){

                    if(radio_male.isChecked())
                        text_gender.setText("M");
                    else if(radio_female.isChecked())
                        text_gender.setText("F");


                    try {
                        MusicianServerApi.changeInfo(MainApplication.getInstance().getCurrentActivity(), //MainApplication.getInstance().getCurrentActivity(),
                                new HttpManager.HttpManagerCallback() {
                                    @Override
                                    public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                                        JSONObject json = new JSONObject(response);
                                        if(json.has("error")) {
                                            Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                                    "Server error", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(json.has("message")){
                                            String mcity = new_city.getText().toString();
                                            String memail = new_email.getText().toString();
                                            String mphone = new_phone.getText().toString();
                                            String mgender = text_gender.getText().toString();

                                            user.setUserInfoData(mgender, mcity, memail, mphone);
                                            UserPrefHelper.setLoggedUser(user.getUserBeanJsonString());

                                            dialog.dismiss();
                                            Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                                    "Info saved successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                },text_gender.getText().toString(), new_city.getText().toString(), new_email.getText().toString(), new_phone.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    text_city.setText(new_city.getText().toString());
                    text_email.setText(new_email.getText().toString());
                    text_phone.setText(new_phone.getText().toString());



                    dialog.dismiss();
                }
                else{
                    Toast.makeText(getActivity(),"Fill in all the empty fields !", Toast.LENGTH_SHORT);
                }
            }
        });
        dialog.show();
    }

    private void user_mod_bio(){
        final Dialog dialog = new Dialog(getActivity());

        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_mod_bio);

        final EditText mbio = dialog.findViewById(R.id.new_bio_text);
        //mbio.setText(user.getBio());
        Button cancel_btn = dialog.findViewById(R.id.cancel_btn);
        Button confirm_btn = dialog.findViewById(R.id.confirm_btn);
        mbio.setText(user.getBio());
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mbio.getText().toString().isEmpty()){
                    try {
                        MusicianServerApi.changeBio(MainApplication.getInstance().getCurrentActivity(), //MainApplication.getInstance().getCurrentActivity(),
                                new HttpManager.HttpManagerCallback() {
                                    @Override
                                    public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                                        JSONObject json = new JSONObject(response);
                                        if(json.has("error")) {
                                            Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                                    "Server error", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(json.has("message")){
                                            String new_bio_text = mbio.getText().toString();

                                            user.setBio(new_bio_text);
                                            UserPrefHelper.setLoggedUser(user.getUserBeanJsonString());
                                            text_bio.setText(new_bio_text);

                                            dialog.dismiss();
                                            Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                                    "Bio saved successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                },mbio.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                else{
                    Toast.makeText(getActivity(),"Insert some bio!", Toast.LENGTH_SHORT);
                }
            }
        });
        dialog.show();
    }

    private void user_friends(){
        FriendsFragment fragment = new FriendsFragment();
        Bundle arguments = new Bundle();
        arguments.putInt( "user_id" , user.getId());
        fragment.setArguments(arguments);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void user_posts(){
        PostsFragment fragment = new PostsFragment();
        Bundle arguments = new Bundle();
        arguments.putInt( "user_id" , user.getId());
        fragment.setArguments(arguments);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    static class ViewHolderSkills {
        public ImageView skill_elem_img;
        public TextView name_skill;
        public TextView count_skills_approved;
        public ImageView endorse_btn;
    }

    private void setupUserSkills(){
        for(int i = 0; i < user.getSkills().length(); i++){
            try {
                JSONObject s = user.getSkills().getJSONObject(i);
                //for(String s :user.getSkills()){
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.row_skill_profile, null);
                final ViewHolderSkills viewHolder = new ViewHolderSkills();
                SkillInfoBean skill = UserPrefHelper.getSkillBean(s.getString("name"));
                viewHolder.name_skill = v.findViewById(R.id.name_skill);
                viewHolder.skill_elem_img = v.findViewById(R.id.skill_elem_img);
                viewHolder.count_skills_approved = v.findViewById(R.id.count_skills_approved);
                viewHolder.endorse_btn = v.findViewById(R.id.endorse_btn);
                viewHolder.skill_elem_img.setImageResource(skill.getIcon_code());
                viewHolder.name_skill.setText(skill.getName());
                viewHolder.count_skills_approved.setText(s.getString("endorse"));
                if(friendship == 3){
                    boolean endorser = false;
                    JSONArray endorsers = s.getJSONArray("endorsers");
                    for(int j = 0; j < endorsers.length(); j++){
                        if((int)endorsers.get(j) == UserPrefHelper.getCurrentUser().getId()){
                            endorser = true;
                        }
                    }
                    if(endorser)
                        viewHolder.endorse_btn.setImageResource(R.drawable.ic_clear);
                    else
                        viewHolder.endorse_btn.setImageResource(R.drawable.ic_check);
                    viewHolder.endorse_btn.setVisibility(View.VISIBLE);
                    viewHolder.endorse_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewHolder.endorse_btn.setEnabled(false);
                            try {
                                MusicianServerApi.endorseSkill(getActivity(), new HttpManager.HttpManagerCallback() {
                                    @Override
                                    public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                                        JSONObject json = new JSONObject(response);
                                        if(json.has("error")){
                                            Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                                    "Server error", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        viewHolder.endorse_btn.setEnabled(true);
                                        viewHolder.count_skills_approved.setText(json.getString("endorse"));
                                        JSONArray endorsers = json.getJSONArray("endorsers");
                                        boolean endorser = false;
                                        for(int j = 0; j < endorsers.length(); j++){
                                            try {
                                                if((int)endorsers.get(j) == UserPrefHelper.getCurrentUser().getId())
                                                    endorser = true;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        if(endorser)
                                            viewHolder.endorse_btn.setImageResource(R.drawable.ic_clear);
                                        else
                                            viewHolder.endorse_btn.setImageResource(R.drawable.ic_check);
                                    }
                                },user.getId(), viewHolder.name_skill.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    });
                }
                v.setTag(viewHolder);
                list_skills.addView(v);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void get_user_info(){
        try {
            MusicianServerApi.getUserInfo(MainApplication.getInstance().getCurrentActivity(),
                    new HttpManager.HttpManagerCallback() {
                        @Override
                        public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                            JSONObject json = new JSONObject(response);
                            if(json.has("message")){
                                if(json.has("bands")){
                                    JSONArray bands_j = json.getJSONArray("bands");
                                    for(int i = 0; i < bands_j.length(); i++){
                                        try {
                                            BandBean band = new BandBean(bands_j.getJSONObject(i).toString());
                                            download_friend_image(band);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                else{
                                    layout_band.setVisibility(View.GONE);
                                }
                                if(json.has("friendship")){
                                    friendship = json.getInt("friendship");
                                    switch (json.getInt("friendship")){
                                        case 0:
                                            friendship_btn.setVisibility(View.VISIBLE);
                                            send_message_btn.setVisibility(View.GONE);
                                            friendship_btn.setText("Add Friend");
                                            friendship_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    set_friendship("Add Friend");
                                                    String mess = user.getId()+"";
                                                    MainApplication.socket.emit("friendship request", mess);
                                                }
                                            });
                                            break;
                                        case 1:
                                            friendship_btn.setVisibility(View.VISIBLE);
                                            send_message_btn.setVisibility(View.GONE);
                                            friendship_btn.setText("Cancel Request");
                                            friendship_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    set_friendship("Cancel Request");
                                                }
                                            });
                                            break;
                                        case 2:
                                            friendship_btn.setVisibility(View.VISIBLE);
                                            send_message_btn.setVisibility(View.VISIBLE);
                                            friendship_btn.setText("Accept Friend");
                                            friendship_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    set_friendship("Accept Friend");
                                                }
                                            });
                                            send_message_btn.setText("Ignore Request");
                                            send_message_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    set_friendship("Cancel Request");
                                                }
                                            });

                                            break;
                                        case 3:
                                            friendship_btn.setVisibility(View.VISIBLE);
                                            send_message_btn.setVisibility(View.VISIBLE);
                                            friendship_btn.setText("Remove Friend");
                                            friendship_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    set_friendship("Cancel Request");
                                                }
                                            });
                                            send_message_btn.setText("Send Message");
                                            send_message_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    go_to_send_message();
                                                }
                                            });
                                            break;
                                    }
                                }
                                setupUserSkills();
                                if(json.has("distance")){
                                    if(json.getInt("distance") == 4)
                                        text_distance.setText("+3°");
                                    else
                                        text_distance.setText(json.getString("distance")+"°");
                                    text_distance.setVisibility(View.VISIBLE);
                                }
                                friends_data = json.getInt("n_friends");
                                post_data = json.getInt("n_posts");
                                text_n_friends.setText(friends_data+" friends");
                                text_n_posts.setText(post_data+" posts");
                            }
                            else{
                                Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                        "Server error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void set_friendship(final String status){
        friendship_btn.setEnabled(false);
        send_message_btn.setEnabled(false);
        try {
            MusicianServerApi.setFriendship(getActivity(), new HttpManager.HttpManagerCallback() {
                @Override
                public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                    JSONObject json = new JSONObject(response);
                    if(json.has("new_status")){
                        if(json.getString("new_status").equals("Add Friend")) {
                            friendship_btn.setText("Add Friend");
                            friendship_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    set_friendship("Add Friend");
                                }
                            });
                            send_message_btn.setVisibility(View.GONE);
                        }
                        else if (json.getString("new_status").equals("Cancel Request")) {
                            friendship_btn.setText("Cancel Request");
                            friendship_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    set_friendship("Cancel Request");
                                }
                            });
                        }
                        else if(json.getString("new_status").equals("Remove Friend")){
                            friendship_btn.setText("Remove Friend");
                            text_distance.setText("1°");
                            friendship_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    set_friendship("Cancel Request");
                                }
                            });
                            send_message_btn.setText("Send Message");
                            send_message_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    go_to_send_message();
                                }
                            });
                            send_message_btn.setVisibility(View.VISIBLE);
                        }
                        friendship_btn.setEnabled(true);
                        send_message_btn.setEnabled(true);
                    }
                    else
                        Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                "Server error", Toast.LENGTH_SHORT).show();
                }
            },user.getId(), status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void go_to_send_message(){
        ChatFragment chatFragment = new ChatFragment(user.getId(),
                user.getFname()+" "+user.getLname(), user.getImg());
        FragmentTransaction fragmentTransaction = ((PortalActivity) MainApplication.getInstance().
                getCurrentActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,chatFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void add_band_layout(final BandBean band){
        CircleImageView band_img = new CircleImageView(getActivity());
        band_img.setImageURI(Uri.fromFile(new File(band.getImg_band())));
        list_band.addView(band_img);
        band_img.getLayoutParams().width = 120;
        band_img.getLayoutParams().height = 120;
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) band_img.getLayoutParams();
        marginParams.setMargins(0, 0, 10, 0);
        band_img.setLayoutParams(marginParams);
        band_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BandFragment bandFragment = new BandFragment(band);

                FragmentTransaction fragmentTransaction = ((PortalActivity) MainApplication.getInstance().
                        getCurrentActivity()).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,bandFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    private void download_friend_image(final BandBean band){
        try {
            File file = new File(MainApplication.getInstance().getCurrentActivity().getApplicationContext().getDir("Images",MODE_PRIVATE),
                    band.getName_band()+".jpg");
            if(file.exists()){
                band.setImg_band(Uri.parse(file.getAbsolutePath()).toString());
                bands.add(band);
                add_band_layout(band);
            }

            else {
                System.out.println("scarico");
                MusicianServerApi.downloadImage(MainApplication.getInstance().getCurrentActivity(), new DownloadTask.DownloadTaskCallback() {
                    @Override
                    public void downloadTaskCallbackResult(Uri imageInternalUri) throws JSONException {
                        band.setImg_band(imageInternalUri.toString());
                        bands.add(band);
                        add_band_layout(band);
                    }
                }, band.getImg_band(), band.getName_band());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
