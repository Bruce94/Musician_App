package me.android.awm.musicianapp;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.fragment.MusicianFragment;
import me.android.awm.musicianapp.utils.UserPrefHelper;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegPhotoFragment extends MusicianFragment {
    public static String FRAGMENT_TAG = "RegPhotoFragment";

    private Button btn_get_pic;
    private Button btn_reg_photo_next;
    private ImageView pic_profile;
    private Uri imageUri;
    private String real_path;

    private static final int PICK_IMAGE = 100;

    @Override
    public void onStart() {
        super.onStart();
        btn_reg_photo_next = (Button) getActivity().findViewById(R.id.btn_reg_photo_next);
        btn_get_pic = (Button) getActivity().findViewById(R.id.btn_get_pic);
        pic_profile = (ImageView) getActivity().findViewById(R.id.pic_profile);
        btn_get_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        btn_reg_photo_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserPrefHelper.setUserPhotoData(real_path);

                workflowListener.manageWorkflow(WorkflowListener.WORKFLOW_ENUM.WORKFLOW_REG_SKILLS);
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

            real_path = getRealPathFromURI(imageUri);
            pic_profile.setImageURI(imageUri);
            pic_profile.setVisibility(View.VISIBLE);
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
}
