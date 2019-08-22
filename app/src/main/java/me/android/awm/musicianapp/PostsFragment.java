package me.android.awm.musicianapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import me.android.awm.musicianapp.adapter.FriendReqAdapter;
import me.android.awm.musicianapp.adapter.PostAdapter;
import me.android.awm.musicianapp.bean.PostBean;
import me.android.awm.musicianapp.bean.UserBean;
import me.android.awm.musicianapp.net.DownloadTask;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;

import static android.content.Context.MODE_PRIVATE;

public class PostsFragment extends Fragment {
    public static String FRAGMENT_TAG = "PostsFragment";

    private PostAdapter adapter;
    private List<PostBean> posts = new LinkedList<PostBean>();
    private ListView list_posts;
    private ProgressBar progressBar;
    private int user_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle arguments = getArguments();
        user_id = arguments.getInt("user_id");
        progressBar = getActivity().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        posts.clear();
        try {
            MusicianServerApi.getUserPosts(MainApplication.getInstance().getCurrentActivity(),
                    new HttpManager.HttpManagerCallback() {
                        @Override
                        public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                            JSONObject json = new JSONObject(response);
                            System.out.println(response);
                            if(json.has("message")){
                                if(json.get("message").equals("yes")){
                                    JSONArray array = json.getJSONArray("data");
                                    for(int i = 0; i < array.length(); i++){
                                        try {
                                            PostBean p = new PostBean(array.get(i).toString());
                                            download_images(p);
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
                            else{
                                Toast.makeText(MainApplication.getInstance().getCurrentActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, user_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        list_posts = getActivity().findViewById(R.id.list_posts);
        adapter = new PostAdapter(getActivity(), posts);
        list_posts.setAdapter(adapter);
    }

    private void download_images(final PostBean post){
        try {
            File file = new File(MainApplication.getInstance().getCurrentActivity().getApplicationContext().getDir("Images",MODE_PRIVATE),
                    post.getUsername()+".jpg");
            if(file.exists()){
                post.setMusician_img(Uri.parse(file.getAbsolutePath()).toString());
                download_post_image(post);
            }
            else {
                MusicianServerApi.downloadImage(MainApplication.getInstance().getCurrentActivity(), new DownloadTask.DownloadTaskCallback() {
                    @Override
                    public void downloadTaskCallbackResult(Uri imageInternalUri) throws JSONException {
                        post.setMusician_img(imageInternalUri.toString());
                        download_post_image(post);
                    }
                }, post.getMusician_img(), post.getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void download_post_image(final PostBean post){
        try {
            if(!post.getPost_image().equals("")) {
                File file = new File(MainApplication.getInstance().getCurrentActivity().getApplicationContext().getDir("Images",MODE_PRIVATE),
                        "post_" + post.getId()+".jpg");
                if(file.exists()){
                    post.setPost_image(Uri.parse(file.getAbsolutePath()).toString());
                    posts.add(post);
                    adapter.notifyDataSetChanged();

                }
                else {
                    MusicianServerApi.downloadImage(MainApplication.getInstance().getCurrentActivity(), new DownloadTask.DownloadTaskCallback() {
                        @Override
                        public void downloadTaskCallbackResult(Uri imageInternalUri) throws JSONException {
                            post.setPost_image(imageInternalUri.toString());
                            posts.add(post);
                            adapter.notifyDataSetChanged();
                        }
                    }, post.getPost_image(), "post_" + post.getId());
                }
            }
            else{
                posts.add(post);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
