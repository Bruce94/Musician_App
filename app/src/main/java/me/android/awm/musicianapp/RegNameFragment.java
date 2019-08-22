package me.android.awm.musicianapp;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.fragment.MusicianFragment;
import me.android.awm.musicianapp.utils.UserPrefHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegNameFragment extends MusicianFragment {
    public static String FRAGMENT_TAG = "RegNameFragment";
    private Button btn_reg_name_next;
    private EditText edit_reg_fname, edit_reg_lname;
    private TextView edit_reg_birth;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private String gender = "";
    private String bday = "";
    private RadioButton radio_male, radio_female;

    @SuppressLint("WrongViewCast")
    @Override
    public void onStart() {
        super.onStart();
        edit_reg_fname = (EditText) getActivity().findViewById(R.id.edit_reg_fname);
        edit_reg_lname = (EditText) getActivity().findViewById(R.id.edit_reg_lname);
        edit_reg_birth = (TextView) getActivity().findViewById(R.id.edit_reg_birth);
        radio_male = (RadioButton) getActivity().findViewById(R.id.radio_male);
        radio_female = (RadioButton) getActivity().findViewById(R.id.radio_female);

        edit_reg_birth.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        MainApplication.getInstance().getCurrentActivity(),
                        mDateSetListener,year,month,day);
                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String date = year + "-" + month + "-" + dayOfMonth;
                edit_reg_birth.setText(date);
            }
        };

        btn_reg_name_next = (Button) getActivity().findViewById(R.id.btn_reg_name_next);
        btn_reg_name_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_reg_fname.getText().toString().isEmpty()) {
                    Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                            "First Name field empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edit_reg_lname.getText().toString().isEmpty()) {
                    Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                            "Last field empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(radio_male.isChecked()){
                    gender = "M";
                }
                else if(radio_female.isChecked()){
                    gender = "F";
                }
                if(!edit_reg_birth.getText().toString().equals("Select Date")){
                    bday = edit_reg_birth.getText().toString();
                }
                UserPrefHelper.setUserPersData(edit_reg_fname.getText().toString(),
                        edit_reg_lname.getText().toString(), bday, gender);

                workflowListener.manageWorkflow(WorkflowListener.WORKFLOW_ENUM.WORKFLOW_REG_MAIL);
            }
        });

    }


}
