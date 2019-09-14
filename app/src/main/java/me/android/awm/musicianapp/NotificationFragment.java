package me.android.awm.musicianapp;


import android.app.Notification;
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

import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.adapter.FriendReqAdapter;
import me.android.awm.musicianapp.adapter.NotificationAdapter;
import me.android.awm.musicianapp.bean.NotificationBean;
import me.android.awm.musicianapp.bean.PostBean;
import me.android.awm.musicianapp.bean.UserBean;
import me.android.awm.musicianapp.net.DownloadTask;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
    public static String FRAGMENT_TAG = "NotificationFragment";

    private List<NotificationBean> notifications = new LinkedList<NotificationBean>();
    private NotificationAdapter adapter;
    private TextView text_no_notifications;
    private ListView list_notifications;
    private ProgressBar progressBar;
    private boolean loaded;

    public NotificationFragment() {
        adapter = new NotificationAdapter(MainApplication.getInstance().getCurrentActivity(), notifications);
        getNotifications();
        loaded = false;
        MainApplication.socket.on("like notification", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    int id = Integer.parseInt(args[0].toString());
                    if(id==UserPrefHelper.getCurrentUser().getId()) {
                        getNotifications();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        getNotifications();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        list_notifications = getActivity().findViewById(R.id.list_notifications);
        text_no_notifications = getActivity().findViewById(R.id.text_no_notifications);
        progressBar = getActivity().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        list_notifications.setAdapter(adapter);
        if(notifications.size() == 0){
            text_no_notifications.setVisibility(View.VISIBLE);
        }
        loaded = true;
    }


    private void download_user_image(final NotificationBean notification){
        try {
            File file = new File(MainApplication.getInstance().getCurrentActivity().getApplicationContext().getDir("Images",MODE_PRIVATE),
                    notification.getUsername()+".jpg");
            if(file.exists()){
                notification.setImg(Uri.parse(file.getAbsolutePath()).toString());
                download_image(notification);
            }
            else {
                MusicianServerApi.downloadImage(MainApplication.getInstance().getCurrentActivity(), new DownloadTask.DownloadTaskCallback() {
                    @Override
                    public void downloadTaskCallbackResult(Uri imageInternalUri) throws JSONException {
                        notification.setImg(imageInternalUri.toString());
                        download_image(notification);

                    }
                }, notification.getImg(), notification.getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void download_image(final NotificationBean notification){
        try {
            if(notification.getPost() != null) {
                if (!notification.getPost().getPost_image().equals("")) {
                    File file = new File(MainApplication.getInstance().
                            getCurrentActivity().getApplicationContext().
                            getDir("Images", MODE_PRIVATE),
                            "post_" + notification.getPost().getId() + ".jpg");
                    if (file.exists()) {
                        notification.getPost().setPost_image(Uri.parse(file.getAbsolutePath()).toString());
                        notifications.add(notification);
                        adapter.notifyDataSetChanged();
                    } else {
                        MusicianServerApi.downloadImage(MainApplication.getInstance().getCurrentActivity(),
                                new DownloadTask.DownloadTaskCallback() {
                            @Override
                            public void downloadTaskCallbackResult(Uri imageInternalUri) throws JSONException {
                                notification.getPost().setPost_image(imageInternalUri.toString());
                                notifications.add(notification);
                                adapter.notifyDataSetChanged();
                            }
                        }, notification.getPost().getPost_image(), "post_" + notification.getPost().getId());
                    }
                } else {
                    notifications.add(notification);
                    adapter.notifyDataSetChanged();
                }
            }
            else if(notification.getBand() != null){
                File file = new File(MainApplication.getInstance().
                        getCurrentActivity().getApplicationContext().
                        getDir("Images", MODE_PRIVATE),
                        notification.getBand().getName_band() + ".jpg");
                if (file.exists()) {
                    notification.getBand().setImg_band(Uri.parse(file.getAbsolutePath()).toString());
                    notifications.add(notification);
                }
                else{
                    MusicianServerApi.downloadImage(MainApplication.getInstance().getCurrentActivity(),
                            new DownloadTask.DownloadTaskCallback() {
                                @Override
                                public void downloadTaskCallbackResult(Uri imageInternalUri) throws JSONException {
                                    notification.getBand().setImg_band(imageInternalUri.toString());
                                    notifications.add(notification);
                                    //adapter.notifyDataSetChanged();
                                }
                            }, notification.getPost().getPost_image(), "post_" + notification.getPost().getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getNotifications(){
        notifications.clear();
        try {
            MusicianServerApi.getNotifications(MainApplication.getInstance().getCurrentActivity(),
                    new HttpManager.HttpManagerCallback() {
                        @Override
                        public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                            JSONObject json = new JSONObject(response);
                            System.out.println("Notification risposto: "+response);
                            if(json.has("message")){
                                if(json.get("message").equals("yes")){
                                    JSONArray array = json.getJSONArray("data");
                                    if(loaded) {
                                        if (array.length() == 0)
                                            text_no_notifications.setVisibility(View.VISIBLE);
                                        else
                                            text_no_notifications.setVisibility(View.GONE);
                                    }
                                    PortalActivity portal_activity = (PortalActivity) MainApplication.getInstance().getCurrentActivity();
                                    Menu menu = portal_activity.mainNav.getMenu();
                                    if(array.length()>0){
                                        menu.findItem(R.id.nav_notification).setIcon(R.drawable.ic_new_notif);
                                    }
                                    else{
                                        menu.findItem(R.id.nav_notification).setIcon(R.drawable.ic_notifications);
                                    }
                                    for(int i = 0; i < array.length(); i++){
                                        try {
                                            NotificationBean n = new NotificationBean(array.get(i).toString());
                                            if(n.getPost() != null)
                                                n.getPost().setMusician_img(UserPrefHelper.getCurrentUser().getImg());
                                            download_user_image(n);
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
