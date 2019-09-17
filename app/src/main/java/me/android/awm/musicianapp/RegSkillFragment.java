package me.android.awm.musicianapp;


import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.android.awm.musicianapp.adapter.SkillsAdapter;
import me.android.awm.musicianapp.bean.SkillInfoBean;
import me.android.awm.musicianapp.bean.UserBean;
import me.android.awm.musicianapp.fragment.MusicianFragment;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegSkillFragment extends MusicianFragment {
    public static String FRAGMENT_TAG = "RegSkillFragment";

    private SkillsAdapter adapter;
    private ArrayList<String> selectedItems = new ArrayList<>();
    private List<SkillInfoBean> skills = new LinkedList<SkillInfoBean>();
    private ListView list_view_skills;
    private Button btn_reg_skill_done;
    private List<String> skills_selected = new LinkedList<>();

    @Override
    public void onStart() {
        super.onStart();

        btn_reg_skill_done = getActivity().findViewById(R.id.btn_reg_skill_done);
        list_view_skills = getActivity().findViewById(R.id.list_skills);
        list_view_skills.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        fillSkillList();

        adapter = new SkillsAdapter(getActivity(), skills);
        list_view_skills.setAdapter(adapter);

        btn_reg_skill_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(SkillInfoBean s : skills){
                    if(s.getState()){
                        skills_selected.add(s.getName());
                    }
                }
                UserPrefHelper.setUserSkillsData(skills_selected);

                try {
                    MusicianServerApi.userRegistration(MainApplication.getInstance().getCurrentActivity(),
                            new HttpManager.HttpManagerCallback() {
                                @Override
                                public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                                    System.out.println("Response: "+response);
                                    UserPrefHelper.clearUserData();
                                    workflowListener.manageWorkflow(WorkflowListener.WORKFLOW_ENUM.WORKFLOW_REG_DONE);
                                }
                            },
                            UserPrefHelper.getUserData());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fillSkillList(){
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
}
