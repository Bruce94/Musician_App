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
import me.android.awm.musicianapp.bean.UserBean;
import me.android.awm.musicianapp.net.DownloadTask;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

import static android.content.Context.MODE_PRIVATE;

public class FriendsFragment extends Fragment {
    public static String FRAGMENT_TAG = "FriendsFragment";

    private FriendReqAdapter adapter;
    private List<UserBean> users = new LinkedList<UserBean>();
    private ListView list_friend;
    private ProgressBar progressBar;
    private int user_id;

    public FriendsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle arguments = getArguments();
        user_id = arguments.getInt("user_id");
        progressBar = getActivity().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        users.clear();
        try {
            MusicianServerApi.getUserFriendsUrl(MainApplication.getInstance().getCurrentActivity(),
                    new HttpManager.HttpManagerCallback() {
                        @Override
                        public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                            JSONObject json = new JSONObject(response);
                            System.out.println("Ha risposto: "+response);
                            if(json.has("message")){
                                if(json.get("message").equals("ok")){
                                    JSONArray array = json.getJSONArray("data");
                                    for(int i = 0; i < array.length(); i++){
                                        try {
                                            System.out.println(i);
                                            UserBean u = new UserBean(array.get(i).toString());
                                            download_friend_image(u);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    adapter.notifyDataSetChanged();
                                }
                                else{
                                    Toast.makeText(MainApplication.getInstance().getCurrentActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(MainApplication.getInstance().getCurrentActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, user_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        list_friend = getActivity().findViewById(R.id.list_friend);
        adapter = new FriendReqAdapter(getActivity(), users);
        list_friend.setAdapter(adapter);
    }

    private void download_friend_image(final UserBean friend){
        try {
            File file = new File(MainApplication.getInstance().getCurrentActivity().getApplicationContext().getDir("Images",MODE_PRIVATE),
                    friend.getUsername()+".jpg");
            if(file.exists()){
                friend.setImg(Uri.parse(file.getAbsolutePath()).toString());
                users.add(friend);
            }
            else {
                System.out.println("scarico");
                MusicianServerApi.downloadImage(MainApplication.getInstance().getCurrentActivity(), new DownloadTask.DownloadTaskCallback() {
                    @Override
                    public void downloadTaskCallbackResult(Uri imageInternalUri) throws JSONException {
                        friend.setImg(imageInternalUri.toString());
                        users.add(friend);
                    }
                }, friend.getImg(), friend.getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
