package me.android.awm.musicianapp;

import android.app.Activity;
import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    public static final String PREFERENCES= "PREFERENCES_MUSICIAN";

    private Activity _currentActivity;
    private static MainApplication _instance;

    public static Socket socket;

    public static synchronized MainApplication getInstance()
    {
        return _instance;
    }

    public void setCurrentActivity(Activity activity)
    {
        _currentActivity = activity;
    }

    public Activity getCurrentActivity(){return _currentActivity;}

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        _currentActivity = null;

        try {
            socket = IO.socket("https://musiciannode.herokuapp.com");
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
}
