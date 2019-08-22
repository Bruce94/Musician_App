package me.android.awm.musicianapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import me.android.awm.musicianapp.adapter.FriendReqAdapter;
import me.android.awm.musicianapp.adapter.MemberAdapter;
import me.android.awm.musicianapp.bean.MemberBean;
import me.android.awm.musicianapp.bean.UserBean;
import me.android.awm.musicianapp.net.DownloadTask;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

import static android.content.Context.MODE_PRIVATE;

public class MembersFragment extends Fragment {
    public static String FRAGMENT_TAG = "MembersFragment";

    private MemberAdapter adapter;
    private List<MemberBean> members = new LinkedList<MemberBean>();
    private ListView list_members;
    private boolean candidates;
    private boolean leader = false;
    private int band_id;
    private ProgressBar progressBar;
    private TextView text_members;
    public MembersFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_members, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle arguments = getArguments();
        candidates = arguments.getBoolean("candidates");
        leader = arguments.getBoolean("leader");
        band_id = arguments.getInt("band_id");
        progressBar = getActivity().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        members.clear();
        try {
            MusicianServerApi.getBandMembers(MainApplication.getInstance().getCurrentActivity(),
                    new HttpManager.HttpManagerCallback() {
                        @Override
                        public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                            JSONObject json = new JSONObject(response);
                            System.out.println(response);
                            if(json.has("members")){
                                JSONArray array = json.getJSONArray("members");
                                for(int i = 0; i < array.length(); i++){
                                    try {
                                        MemberBean m = new MemberBean(array.get(i).toString());
                                        download_member_image(m);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                            else{
                                Toast.makeText(MainApplication.getInstance().getCurrentActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, band_id, candidates);
        } catch (Exception e) {
            e.printStackTrace();
        }
        list_members = getActivity().findViewById(R.id.list_members);
        text_members = getActivity().findViewById(R.id.text_members);
        if(candidates)
            text_members.setText("Candidates:");

        adapter = new MemberAdapter(getActivity(), members, leader);
        list_members.setAdapter(adapter);
    }

    private void download_member_image(final MemberBean member){
        try {
            File file = new File(MainApplication.getInstance().getCurrentActivity().getApplicationContext().getDir("Images",MODE_PRIVATE),
                    member.getUser().getUsername()+".jpg");
            if(file.exists()){
                member.getUser().setImg(Uri.parse(file.getAbsolutePath()).toString());
                members.add(member);
            }
            else {
                MusicianServerApi.downloadImage(MainApplication.getInstance().getCurrentActivity(), new DownloadTask.DownloadTaskCallback() {
                    @Override
                    public void downloadTaskCallbackResult(Uri imageInternalUri) throws JSONException {
                        member.getUser().setImg(imageInternalUri.toString());
                        members.add(member);
                    }
                }, member.getUser().getImg(), member.getUser().getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
