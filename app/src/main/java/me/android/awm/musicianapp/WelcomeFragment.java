package me.android.awm.musicianapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.fragment.MusicianFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment extends MusicianFragment {
    public static String FRAGMENT_TAG = "WelcomeFragment";

    private Button btn_reg_wel_next;

    @Override
    public void onStart() {
        super.onStart();

        btn_reg_wel_next = (Button) getActivity().findViewById(R.id.btn_reg_wel_next);
        btn_reg_wel_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workflowListener.manageWorkflow(WorkflowListener.WORKFLOW_ENUM.WORKFLOW_REG_NAME);
            }
        });
    }
}
