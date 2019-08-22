package me.android.awm.musicianapp;

import android.net.Uri;
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

import me.android.awm.musicianapp.adapter.BandAdapter;
import me.android.awm.musicianapp.adapter.FriendReqAdapter;
import me.android.awm.musicianapp.bean.BandBean;
import me.android.awm.musicianapp.bean.UserBean;
import me.android.awm.musicianapp.net.DownloadTask;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;

import static android.content.Context.MODE_PRIVATE;

public class BandListFragment extends Fragment {
    public static String FRAGMENT_TAG = "BandListFragment";

    private BandAdapter adapter;
    private List<BandBean> bands = new LinkedList<BandBean>();
    private ListView list_bands;
    private TextView text_no_found;
    private String query;
    private Button band_filter_btn;
    private ProgressBar progressBar;
    private List<String> checked_skills = new LinkedList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_band_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle arguments = getArguments();
        query = arguments.getString("query","");
        if(arguments.containsKey("checked_skills"))
            checked_skills = arguments.getStringArrayList("checked_skills");
        list_bands = getActivity().findViewById(R.id.list_bands);
        text_no_found = getActivity().findViewById(R.id.text_no_found);
        band_filter_btn = getActivity().findViewById(R.id.band_filter_btn);
        progressBar = getActivity().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        bands.clear();
        adapter = new BandAdapter(getActivity(), bands);
        list_bands.setAdapter(adapter);
        try {
            MusicianServerApi.bandList(MainApplication.getInstance().getCurrentActivity(),
                    new HttpManager.HttpManagerCallback() {
                        @Override
                        public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                            JSONObject json = new JSONObject(response);
                            System.out.println("Ha risposto: "+response);

                            if(json.has("bands")){
                                JSONArray array = json.getJSONArray("bands");
                                if(array.length()==0){
                                    text_no_found.setText("No musicians were found");
                                    text_no_found.setVisibility(View.VISIBLE);
                                }
                                for(int i = 0; i < array.length(); i++){
                                    try {
                                        BandBean b = new BandBean(array.get(i).toString());
                                        download_band_image(b);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                            else{
                                Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                        "Server Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },query, checked_skills);
        } catch (Exception e) {
            e.printStackTrace();
        }

        band_filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterBandFragment fragment = new FilterBandFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    private void download_band_image(final BandBean b){
        try {
            File file = new File(MainApplication.getInstance().getCurrentActivity().getApplicationContext().getDir("Images",MODE_PRIVATE),
                    b.getName_band()+".jpg");
            if(file.exists()){
                b.setImg_band(Uri.parse(file.getAbsolutePath()).toString());
                bands.add(b);
            }
            else {
                MusicianServerApi.downloadImage(MainApplication.getInstance().getCurrentActivity(), new DownloadTask.DownloadTaskCallback() {
                    @Override
                    public void downloadTaskCallbackResult(Uri imageInternalUri) throws JSONException {
                        b.setImg_band(imageInternalUri.toString());
                        bands.add(b);
                    }
                }, b.getImg_band(), b.getName_band());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
