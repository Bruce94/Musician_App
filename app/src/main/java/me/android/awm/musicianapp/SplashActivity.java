package me.android.awm.musicianapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.android.awm.musicianapp.utils.UserPrefHelper;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2500;
    public static String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().setCurrentActivity(this);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    Intent intent;
                    if(UserPrefHelper.checkIfUserIsLogged()){
                        intent = new Intent(SplashActivity.this, PortalActivity.class);
                    }
                    else{
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }catch (Exception exc) {
                    exc.printStackTrace();
                }

            }
        }, SPLASH_TIME_OUT);
    }

}
