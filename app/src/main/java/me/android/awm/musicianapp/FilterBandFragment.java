package me.android.awm.musicianapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.android.awm.musicianapp.adapter.SkillsAdapter;
import me.android.awm.musicianapp.bean.SkillInfoBean;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

public class FilterBandFragment extends Fragment {
    public static String FRAGMENT_TAG = "FilterBandFragment";

    private SkillsAdapter adapter;
    private EditText edit_query_band;
    private ArrayList<String> selectedItems = new ArrayList<>();
    private List<SkillInfoBean> skills = new LinkedList<SkillInfoBean>();
    private ListView list_view_skills;
    private Button btn_apply;
    private ArrayList<String> skills_selected = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter_band, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        btn_apply = getActivity().findViewById(R.id.btn_apply);
        list_view_skills = getActivity().findViewById(R.id.list_skills);
        edit_query_band = getActivity().findViewById(R.id.edit_query_band);
        list_view_skills.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        fillSkillList();

        adapter = new SkillsAdapter(getActivity(), skills);
        list_view_skills.setAdapter(adapter);
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(SkillInfoBean s : skills){
                    if(s.getState()){
                        skills_selected.add(s.getName());
                    }
                }
                BandListFragment fragment = new BandListFragment();
                Bundle arguments = new Bundle();
                arguments.putString("query", edit_query_band.getText().toString());
                arguments.putStringArrayList("checked_skills",skills_selected);
                fragment.setArguments(arguments);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
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
