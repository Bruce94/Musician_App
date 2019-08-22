package me.android.awm.musicianapp;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.fragment.MusicianFragment;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegMailPassFragment extends MusicianFragment {
    public static String FRAGMENT_TAG = "RegMailPassFragment";

    private Button btn_reg_email_next;
    private EditText edit_reg_username, edit_reg_email, edit_reg_pwd, edit_reg_conf_pwd;

    @Override
    public void onStart() {
        super.onStart();

        edit_reg_username = (EditText) getActivity().findViewById(R.id.edit_reg_username);
        edit_reg_email = (EditText) getActivity().findViewById(R.id.edit_reg_email);
        edit_reg_pwd = (EditText) getActivity().findViewById(R.id.edit_reg_pwd);
        edit_reg_conf_pwd = (EditText) getActivity().findViewById(R.id.edit_reg_conf_pwd);
        btn_reg_email_next = (Button) getActivity().findViewById(R.id.btn_reg_email_next);

        btn_reg_email_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_reg_username.getText().toString().isEmpty()) {
                    Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                            "Username field empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edit_reg_email.getText().toString().isEmpty()) {
                    Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                            "Email field empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidEmail(edit_reg_email.getText().toString())){
                    Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                            "Email format is not correct", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (edit_reg_pwd.getText().toString().isEmpty()) {
                    Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                            "Password field empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!(edit_reg_pwd.getText().toString().equals(
                        edit_reg_conf_pwd.getText().toString()))){
                    Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                            "Passwords doesn't match", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    MusicianServerApi.checkUsername(MainApplication.getInstance().getCurrentActivity(),
                            new HttpManager.HttpManagerCallback() {
                                @Override
                                public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                                    JSONObject json = new JSONObject(response);
                                    if(json.has("message")){
                                        if(json.get("message").equals("no")){

                                            UserPrefHelper.setUserAuthData(
                                                    edit_reg_username.getText().toString(),
                                                    edit_reg_pwd.getText().toString(),
                                                    edit_reg_email.getText().toString());
                                            workflowListener.manageWorkflow(WorkflowListener.WORKFLOW_ENUM.WORKFLOW_REG_GENERAL);
                                        }
                                        else if(json.get("message").equals("yes")) {
                                            Toast.makeText(MainApplication.getInstance().getCurrentActivity(), "Username Aleready exists", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        Toast.makeText(MainApplication.getInstance().getCurrentActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },edit_reg_username.getText().toString());
                } catch (JSONException e) {
                    System.out.println("Json Error!"+ e);
                }
            }
        });
    }

    private boolean isValidEmail(String target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
