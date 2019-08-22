package me.android.awm.musicianapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.android.awm.musicianapp.bean.UserBean;
import me.android.awm.musicianapp.net.DownloadTask;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

public class LoginActivity extends AppCompatActivity {

    private Button login_btn;
    private Button create_prof_btn;
    private EditText edit_log_username;
    private EditText edit_log_pass;
    private JSONObject jsonData;
    private ProgressBar progressBarLogin;
    private String server_img;


    //private HttpClient httpClient = new DefaultHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_btn = (Button) findViewById(R.id.log_button);
        create_prof_btn = (Button) findViewById(R.id.log_create_prof);
        edit_log_username = (EditText)findViewById(R.id.log_username);
        edit_log_pass = (EditText) findViewById(R.id.log_pass);
        progressBarLogin = (ProgressBar) findViewById(R.id.progressBarLogin);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_log_username.getText().toString().isEmpty()){
                    Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                            "User field empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                login_btn.setEnabled(false);
                progressBarLogin.setVisibility(View.VISIBLE);
                try{
                    MusicianServerApi.loginUser(MainApplication.getInstance().getCurrentActivity(), new HttpManager.HttpManagerCallback() {
                        @Override
                        public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                            JSONObject json = new JSONObject(response);
                            System.out.println("Ha rispostox " + json);
                            if(json.has("message")){
                                System.out.println(json.get("message"));
                                if(json.get("message").equals("ok")){
                                    jsonData = json.getJSONObject("data");
                                    jsonData.accumulate("pwd", edit_log_pass.getText().toString());
                                    jsonData.accumulate("username", edit_log_username.getText().toString());
                                    download_profile_image();
                                }
                                else{
                                    Toast.makeText(MainApplication.getInstance().getCurrentActivity(), "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                                    progressBarLogin.setVisibility(View.GONE);
                                    login_btn.setEnabled(true);
                                }
                            }
                            else{
                                Toast.makeText(MainApplication.getInstance().getCurrentActivity(), "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                                progressBarLogin.setVisibility(View.GONE);
                                login_btn.setEnabled(true);
                            }
                        }
                    },edit_log_username.getText().toString(), edit_log_pass.getText().toString());
                }
                catch (Exception exc) {
                    System.out.println("Json Error!"+ exc);
                }
            }
        });

        create_prof_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
            }
        });

    }

    private void download_profile_image(){
        try {
            server_img = jsonData.getString("img");
            System.out.println();
            MusicianServerApi.downloadImage(MainApplication.getInstance().getCurrentActivity(), new DownloadTask.DownloadTaskCallback() {
                @Override
                public void downloadTaskCallbackResult(Uri imageInternalUri) throws JSONException {
                    jsonData.put("img", imageInternalUri.toString());
                    try {
                        UserBean user = new UserBean(jsonData.toString());
                        user.setServer_img(server_img);
                        UserPrefHelper.setLoggedUser(user.getUserBeanJsonString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(imageInternalUri);
                    Intent i = new Intent(LoginActivity.this, PortalActivity.class);
                    startActivity(i);
                    //progressBarLogin.setVisibility(View.GONE);
                    finish();
                }
            },jsonData.get("img").toString(),"image_profile");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
