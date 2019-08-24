package me.android.awm.musicianapp;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import me.android.awm.musicianapp.adapter.PostAdapter;
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
public class HomeFragment extends Fragment {
    public static String FRAGMENT_TAG = "ProfileFragment";


    private UserBean user;
    private PostAdapter adapter;
    private CircleImageView pic_profile;
    private TextView suggestions_txt;
    private List<PostBean> posts = new LinkedList<PostBean>();
    private ListView post_list;
    private ProgressBar progressBar;
    private LinearLayout create_post_btn;

    public HomeFragment() {
        adapter = new PostAdapter(MainApplication.getInstance().getCurrentActivity(), posts);
        try {
            user = new UserBean(UserPrefHelper.getLoggedUser());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //getHomePosts();

        MainApplication.socket.on("post arrived", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                getHomePosts();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        post_list = getActivity().findViewById(R.id.post_list);
        pic_profile = getActivity().findViewById(R.id.pic_profile_home);
        create_post_btn = getActivity().findViewById(R.id.create_post_btn);
        pic_profile.setImageURI(Uri.fromFile(new File(user.getImg())));
        suggestions_txt = getActivity().findViewById(R.id.suggestions_txt);
        suggestions_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suggested_friends();
            }
        });
        create_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_post();
            }
        });
        post_list.setAdapter(adapter);
        progressBar = getActivity().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void suggested_friends(){
        SuggestedFragment suggestedFragment = new SuggestedFragment();

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,suggestedFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void create_post(){
        CreatePostFragment createPostFragment = new CreatePostFragment();

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,createPostFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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


    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        getHomePosts();
    }

    private void getHomePosts(){
        posts.clear();
//        adapter.notifyDataSetChanged();
        try {
            MusicianServerApi.home_posts(MainApplication.getInstance().getCurrentActivity(),
                    new HttpManager.HttpManagerCallback() {
                        @Override
                        public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                            JSONObject json = new JSONObject(response);
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
