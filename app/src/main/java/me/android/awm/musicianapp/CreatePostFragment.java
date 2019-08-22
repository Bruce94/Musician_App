package me.android.awm.musicianapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import me.android.awm.musicianapp.bean.UserBean;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

import static android.app.Activity.RESULT_OK;

public class CreatePostFragment extends Fragment {
    public static String FRAGMENT_TAG = "RegPhotoFragment";

    private CircleImageView pic_profile;
    private TextView user_txt;
    private EditText post_edit_txt;
    private ImageView imv_post_image;
    private Button photo_btn;
    private Button new_post_btn;
    private Uri imageUri;
    private String real_path = "";
    private UserBean user;

    private static final int PICK_IMAGE = 100;

    public CreatePostFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_post, container, false);
    }

    public void onStart() {
        super.onStart();
        try {
            user = UserPrefHelper.getCurrentUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
        pic_profile = getActivity().findViewById(R.id.pic_profile);
        user_txt = getActivity().findViewById(R.id.user_txt);
        post_edit_txt = getActivity().findViewById(R.id.post_edit_txt);
        imv_post_image = getActivity().findViewById(R.id.imv_post_image);
        photo_btn = getActivity().findViewById(R.id.photo_btn);
        new_post_btn = getActivity().findViewById(R.id.new_post_btn);

        pic_profile.setImageURI(Uri.fromFile(new File(user.getImg())));
        user_txt.setText(user.getFname()+" "+user.getLname());

        post_edit_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(s);
                if(s.toString().equals("") && real_path.isEmpty()){
                    new_post_btn.setEnabled(false);
                } else {
                    new_post_btn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        new_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MusicianServerApi.newPost(MainApplication.getInstance().getCurrentActivity(), new HttpManager.HttpManagerCallback() {
                        @Override
                        public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                            JSONObject json = new JSONObject(response);
                            if(json.has("message")){
                                MainApplication.socket.emit("post sent");
                                return_home();
                            }
                            else{
                                Toast.makeText(MainApplication.getInstance().getCurrentActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },post_edit_txt.getText().toString(),real_path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            new_post_btn.setEnabled(true);
            real_path = getRealPathFromURI(imageUri);
            imv_post_image.setImageURI(imageUri);
            imv_post_image.setVisibility(View.VISIBLE);
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void return_home(){
        HomeFragment homeFragment = new HomeFragment();

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,homeFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
