package me.android.awm.musicianapp;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.adapter.ChatAdapter;
import me.android.awm.musicianapp.adapter.FriendReqAdapter;
import me.android.awm.musicianapp.bean.ChatBean;
import me.android.awm.musicianapp.bean.UserBean;
import me.android.awm.musicianapp.net.DownloadTask;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {
    public static String FRAGMENT_TAG = "MessageFragment";

    private ChatAdapter adapter;
    private List<ChatBean> chats = new LinkedList<ChatBean>();
    private ListView list_chats;
    private ProgressBar progressBarChats;
    private boolean loaded;

    public MessageFragment() {
        get_chats();
        loaded = false;
        adapter = new ChatAdapter(MainApplication.getInstance().getCurrentActivity(), chats);
        MainApplication.socket.on("message arrived", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONArray data;
                    if(args[0].getClass() == String.class){
                        String[] parts = args[0].toString().split(",");
                        data = new JSONArray();
                        for(String part:parts){
                            data.put(part);
                        }
                    }
                    else
                        data = new JSONArray(args[0].toString());
                    if(data.getInt(1)==UserPrefHelper.getCurrentUser().getId()) {
                        get_chats();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
        return inflater.inflate(R.layout.fragment_message, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();
        loaded = true;
        list_chats = getActivity().findViewById(R.id.list_chats);
        progressBarChats = getActivity().findViewById(R.id.progressBarChats);
        list_chats.setAdapter(adapter);
        progressBarChats.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBarChats.setVisibility(View.VISIBLE);
        get_chats();
    }

    private void get_chats(){
        chats.clear();
        //adapter.notifyDataSetChanged();
        try {
            MusicianServerApi.get_chats(MainApplication.getInstance().getCurrentActivity(),
                    new HttpManager.HttpManagerCallback() {
                        @Override
                        public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                            JSONObject json = new JSONObject(response);
                            if(json.has("chats")){
                                JSONArray array = json.getJSONArray("chats");
                                for(int i = 0; i < array.length(); i++){
                                    try {
                                        ChatBean u = new ChatBean(array.get(i).toString());
                                        download_musician_image(u);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if(loaded)
                                    progressBarChats.setVisibility(View.GONE);
                            }
                            else{
                                Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                        "Server Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void download_musician_image(final ChatBean chat){
        try {
            File file = new File(MainApplication.getInstance().getCurrentActivity().getApplicationContext().getDir("Images",MODE_PRIVATE),
                    chat.getUsername()+".jpg");
            if(file.exists()){
                chat.setMusician_img(Uri.parse(file.getAbsolutePath()).toString());
                chats.add(chat);
                adapter.notifyDataSetChanged();
            }
            else {
                MusicianServerApi.downloadImage(MainApplication.getInstance().getCurrentActivity(), new DownloadTask.DownloadTaskCallback() {
                    @Override
                    public void downloadTaskCallbackResult(Uri imageInternalUri) throws JSONException {
                        chat.setMusician_img(imageInternalUri.toString());
                        chats.add(chat);
                        adapter.notifyDataSetChanged();
                    }
                }, chat.getMusician_img(), chat.getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
