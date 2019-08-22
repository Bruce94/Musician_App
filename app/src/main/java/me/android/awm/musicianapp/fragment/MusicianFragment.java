package me.android.awm.musicianapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.android.awm.musicianapp.WorkflowListener;

public class MusicianFragment extends Fragment {
    public static String RES_LAYOUT_ID_PARAM_NAME = "RES_LAYOUT_ID_PARAM_NAME";

    protected Activity context;
    protected WorkflowListener workflowListener;

    public MusicianFragment(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.context = (Activity) context;
            workflowListener = ( WorkflowListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement WorkflowListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getArguments().getInt(RES_LAYOUT_ID_PARAM_NAME),container,false);
    }
}
