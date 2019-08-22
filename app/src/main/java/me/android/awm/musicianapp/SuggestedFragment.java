package me.android.awm.musicianapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import me.android.awm.musicianapp.bean.UserBean;
import me.android.awm.musicianapp.net.DownloadTask;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

import static android.content.Context.MODE_PRIVATE;

public class SuggestedFragment extends Fragment {
    public static String FRAGMENT_TAG = "SuggestedFragment";

    private FriendReqAdapter adapter_friend, adapter_tag;
    private TextView text_no_sfriend_found, text_no_stag_found;
    private List<UserBean> users_friend = new LinkedList<UserBean>();
    private List<UserBean> users_tag = new LinkedList<UserBean>();
    private ListView list_suggested_tag, list_suggested;
    private ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_suggested, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        list_suggested_tag = getActivity().findViewById(R.id.list_suggested_tag);
        list_suggested = getActivity().findViewById(R.id.list_suggested);
        progressBar = getActivity().findViewById(R.id.progressBar);
        text_no_sfriend_found = getActivity().findViewById(R.id.text_no_sfriend_found);
        text_no_stag_found = getActivity().findViewById(R.id.text_no_stag_found);

        adapter_friend = new FriendReqAdapter(getActivity(), users_friend);
        adapter_tag = new FriendReqAdapter(getActivity(), users_tag);
        users_friend.clear();
        users_tag.clear();
        list_suggested.setAdapter(adapter_friend);
        list_suggested_tag.setAdapter(adapter_tag);
        progressBar.setVisibility(View.VISIBLE);

        try {
            MusicianServerApi.suggested(MainApplication.getInstance().getCurrentActivity(), new HttpManager.HttpManagerCallback() {
                @Override
                public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                    JSONObject json = new JSONObject(response);

                    if(json.has("message")){
                        if(json.get("message").equals("ok")){
                            JSONArray array_f = json.getJSONArray("suggested_friends");
                            JSONArray array_t = json.getJSONArray("suggested_friends_tag");

                            if(array_f.length()==0)
                                text_no_sfriend_found.setVisibility(View.VISIBLE);
                            if(array_t.length()==0)
                                text_no_stag_found.setVisibility(View.VISIBLE);
                            for(int i = 0; i < array_f.length(); i++){
                                try {
                                    UserBean u = new UserBean(array_f.get(i).toString());
                                    download_suggested_image(u,1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            adapter_friend.notifyDataSetChanged();
                            for(int i = 0; i < array_t.length(); i++){
                                try {
                                    UserBean u = new UserBean(array_t.get(i).toString());
                                    download_suggested_image(u,2);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            adapter_tag.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                        else{
                            Toast.makeText(MainApplication.getInstance().getCurrentActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(MainApplication.getInstance().getCurrentActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }, UserPrefHelper.getCurrentUser().getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void download_suggested_image(final UserBean founded, int type){
        try {
            File file = new File(MainApplication.getInstance().getCurrentActivity().getApplicationContext().getDir("Images",MODE_PRIVATE),
                    founded.getUsername()+".jpg");
            if(file.exists()){
                founded.setImg(Uri.parse(file.getAbsolutePath()).toString());
                if(type == 1)
                    users_friend.add(founded);
                else
                    users_tag.add(founded);
            }
            else {
                if(type ==1)
                    MusicianServerApi.downloadImage(MainApplication.getInstance().getCurrentActivity(), new DownloadTask.DownloadTaskCallback() {
                        @Override
                        public void downloadTaskCallbackResult(Uri imageInternalUri) throws JSONException {
                            founded.setImg(imageInternalUri.toString());
                                users_friend.add(founded);
                        }
                    }, founded.getImg(), founded.getUsername());
                else
                    MusicianServerApi.downloadImage(MainApplication.getInstance().getCurrentActivity(), new DownloadTask.DownloadTaskCallback() {
                        @Override
                        public void downloadTaskCallbackResult(Uri imageInternalUri) throws JSONException {
                            founded.setImg(imageInternalUri.toString());
                            users_tag.add(founded);
                        }
                    }, founded.getImg(), founded.getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
