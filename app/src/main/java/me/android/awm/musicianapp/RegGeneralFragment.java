package me.android.awm.musicianapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.fragment.MusicianFragment;
import me.android.awm.musicianapp.utils.UserPrefHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegGeneralFragment extends MusicianFragment {
    public static String FRAGMENT_TAG = "RegGeneralFragment";

    public static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^\\+?1?\\d{9,15}$");

    private Button btn_reg_general_next;
    private EditText edit_reg_phone;
    private EditText edit_reg_city,edit_reg_country;

    @Override
    public void onStart() {
        super.onStart();
        edit_reg_phone = (EditText) getActivity().findViewById(R.id.edit_reg_phone);
        edit_reg_country = (EditText) getActivity().findViewById(R.id.edit_reg_country);
        edit_reg_city = (EditText) getActivity().findViewById(R.id.edit_reg_city);
        btn_reg_general_next = (Button) getActivity().findViewById(R.id.btn_reg_general_next);

        btn_reg_general_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isValidPhone(edit_reg_phone.getText().toString())){
                    Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                            "Phone format is not correct", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserPrefHelper.setUserGeneralData(edit_reg_phone.getText().toString(),
                        edit_reg_country.getText().toString(),
                        edit_reg_city.getText().toString());

                workflowListener.manageWorkflow(WorkflowListener.WORKFLOW_ENUM.WORKFLOW_REG_BIO);
            }
        });
    }

    private boolean isValidPhone(String target) {
        return PHONE_NUMBER_PATTERN.matcher(target).matches();
    }

}
