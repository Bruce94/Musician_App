package me.android.awm.musicianapp;

import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import static android.content.Context.MODE_PRIVATE;

public class SearchFragment extends Fragment {
    public static String FRAGMENT_TAG = "SearchFragment";

    private FriendReqAdapter adapter;
    private TextView text_no_found;
    private List<UserBean> users = new LinkedList<UserBean>();
    private ListView list_musicians;
    private ProgressBar progressBar;
    private Button filter_btn;
    private String query;
    private List<String> checked_skills = new LinkedList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle arguments = getArguments();
        query = arguments.getString("query");
        if(arguments.containsKey("checked_skills"))
            checked_skills = arguments.getStringArrayList("checked_skills");
        list_musicians = getActivity().findViewById(R.id.list_musicians);
        text_no_found = getActivity().findViewById(R.id.text_no_found);
        progressBar = getActivity().findViewById(R.id.progressBar);
        filter_btn = getActivity().findViewById(R.id.filter_btn);
        filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply_filter();
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        adapter = new FriendReqAdapter(getActivity(), users);
        list_musicians.setAdapter(adapter);
        users.clear();

        try {
            JSONObject jsonQuery = new JSONObject();
            MusicianServerApi.searchMusicians(MainApplication.getInstance().getCurrentActivity(),
                    new HttpManager.HttpManagerCallback() {
                        @Override
                        public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                            JSONObject json = new JSONObject(response);
                            System.out.println("Ha risposto: "+response);

                            if(json.has("message")){
                                if(json.get("message").equals("ok")){
                                    JSONArray array = json.getJSONArray("data");
                                    if(array.length()==0){
                                        text_no_found.setText("No musicians were found for "+query);
                                        text_no_found.setVisibility(View.VISIBLE);
                                    }
                                    for(int i = 0; i < array.length(); i++){
                                        try {
                                            UserBean u = new UserBean(array.get(i).toString());
                                            download_friend_image(u);
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
                    },query,checked_skills);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void download_friend_image(final UserBean founded){
        try {
            File file = new File(MainApplication.getInstance().getCurrentActivity().getApplicationContext().getDir("Images",MODE_PRIVATE),
                    founded.getUsername()+".jpg");
            if(file.exists()){
                founded.setImg(Uri.parse(file.getAbsolutePath()).toString());
                users.add(founded);
            }
            else {
                MusicianServerApi.downloadImage(MainApplication.getInstance().getCurrentActivity(), new DownloadTask.DownloadTaskCallback() {
                    @Override
                    public void downloadTaskCallbackResult(Uri imageInternalUri) throws JSONException {
                        founded.setImg(imageInternalUri.toString());
                        users.add(founded);
                    }
                }, founded.getImg(), founded.getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void apply_filter(){
        FilterBandFragment fragment = new FilterBandFragment();
        Bundle arguments = new Bundle();
        arguments.putString( "name" , query);
        arguments.putInt( "type" , 2);
        fragment.setArguments(arguments);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}
