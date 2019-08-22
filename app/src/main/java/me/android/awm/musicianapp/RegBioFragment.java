package me.android.awm.musicianapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.fragment.MusicianFragment;
import me.android.awm.musicianapp.utils.UserPrefHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegBioFragment extends MusicianFragment {
    public static String FRAGMENT_TAG = "RegBioFragment";

    private Button btn_reg_bio_next;
    private EditText edit_reg_bio;


    @Override
    public void onStart() {
        super.onStart();
        edit_reg_bio = (EditText) getActivity().findViewById(R.id.edit_reg_bio);
        btn_reg_bio_next = (Button) getActivity().findViewById(R.id.btn_reg_bio_next);

        btn_reg_bio_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserPrefHelper.setUserBioData(edit_reg_bio.getText().toString());

                workflowListener.manageWorkflow(WorkflowListener.WORKFLOW_ENUM.WORKFLOW_REG_PHOTO);
            }
        });

    }
}
