package me.android.awm.musicianapp;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.adapter.FriendReqAdapter;
import me.android.awm.musicianapp.adapter.SkillsAdapter;
import me.android.awm.musicianapp.bean.UserBean;
import me.android.awm.musicianapp.net.DownloadTask;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReqFriendFragment extends Fragment {
    public static String FRAGMENT_TAG = "ReqFriendFragment";

    private FriendReqAdapter adapter;
    private TextView text_no_requests;
    private List<UserBean> users = new LinkedList<UserBean>();
    private ListView list_friend_req;
    private ProgressBar progressBar;
    private boolean loaded;

    public ReqFriendFragment() {
        adapter = new FriendReqAdapter(MainApplication.getInstance().getCurrentActivity(), users);
        get_friendhip_req_list();
        loaded = false;
        MainApplication.socket.on("friendship notification", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    int id = Integer.parseInt(args[0].toString());
                    if(id==UserPrefHelper.getCurrentUser().getId()) {
                        get_friendhip_req_list();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_req_friend, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        PortalActivity portal_activity = (PortalActivity) MainApplication.getInstance().getCurrentActivity();
        Menu menu = portal_activity.mainNav.getMenu();
        menu.findItem(R.id.nav_req_friend).setIcon(R.drawable.ic_friends);
        list_friend_req = getActivity().findViewById(R.id.list_friend_req);
        text_no_requests = getActivity().findViewById(R.id.text_no_requests);
        progressBar = getActivity().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        list_friend_req.setAdapter(adapter);
        if(users.size() == 0){
            text_no_requests.setVisibility(View.VISIBLE);
        }
        loaded = true;
    }

    private void download_friend_image(final UserBean friend){
        try {
            File file = new File(MainApplication.getInstance().getCurrentActivity().getApplicationContext().getDir("Images",MODE_PRIVATE),
                    friend.getUsername()+".jpg");
            if(file.exists()){
                friend.setImg(Uri.parse(file.getAbsolutePath()).toString());
                users.add(friend);
                adapter.notifyDataSetChanged();
            }
            else {
                MusicianServerApi.downloadImage(MainApplication.getInstance().getCurrentActivity(), new DownloadTask.DownloadTaskCallback() {
                    @Override
                    public void downloadTaskCallbackResult(Uri imageInternalUri) throws JSONException {
                        friend.setImg(imageInternalUri.toString());
                        users.add(friend);
                        adapter.notifyDataSetChanged();
                    }
                }, friend.getImg(), friend.getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        get_friendhip_req_list();
    }


    private void get_friendhip_req_list(){
        users.clear();
        try {
            MusicianServerApi.getFrirenshipReq(MainApplication.getInstance().getCurrentActivity(),
                    new HttpManager.HttpManagerCallback() {
                        @Override
                        public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                            JSONObject json = new JSONObject(response);
                            if(json.has("message")){
                                if(json.get("message").equals("ok")){
                                    JSONArray array = json.getJSONArray("data");
                                    if(loaded) {
                                        if (array.length() == 0)
                                            text_no_requests.setVisibility(View.VISIBLE);
                                        else
                                            text_no_requests.setVisibility(View.GONE);
                                    }
                                    else{
                                        PortalActivity portal_activity = (PortalActivity) MainApplication.getInstance().getCurrentActivity();
                                        Menu menu = portal_activity.mainNav.getMenu();
                                        if(array.length()>0){
                                            menu.findItem(R.id.nav_req_friend).setIcon(R.drawable.ic_new_friend);
                                        }
                                        else{
                                            menu.findItem(R.id.nav_req_friend).setIcon(R.drawable.ic_friends);
                                        }
                                    }
                                    for(int i = 0; i < array.length(); i++){
                                        try {
                                            UserBean u = new UserBean(array.get(i).toString());
                                            download_friend_image(u);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if(loaded)
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
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
