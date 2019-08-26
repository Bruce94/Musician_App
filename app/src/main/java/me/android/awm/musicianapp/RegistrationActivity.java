package me.android.awm.musicianapp;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.FrameLayout;

import me.android.awm.musicianapp.fragment.MusicianFragment;
import me.android.awm.musicianapp.utils.UserPrefHelper;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RegistrationActivity extends AppCompatActivity implements WorkflowListener{
    public static String TAG = "PortalActivity";

    public WORKFLOW_ENUM workflow;

    private FrameLayout registrationFrame;

    //private WelcomeFragment welcomeFragment;
    //private RegNameFragment regNameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().setCurrentActivity(this);
        setContentView(R.layout.activity_registration);

        registrationFrame = (FrameLayout) findViewById(R.id.fragment_registration);

        manageWorkflow(WORKFLOW_ENUM.WORKFLOW_REG_WEL);

        if (!checkPermission()) {
            openActivity();
        } else {
            if (checkPermission()) {
                requestPermissionAndContinue();
            } else {
                openActivity();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if ((keyCode == KeyEvent.KEYCODE_BACK)) { //stop your music here
            // To exit application
            onBackPressed_reg();
            return true;
        }
        else
        {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void onBackPressed_reg() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Account Creation")
                .setMessage("Are you sure you want to cancel account creation? This will discard any information you've entered so far")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        UserPrefHelper.clearUserData();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed_reg();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void manageWorkflow(WORKFLOW_ENUM workflow) {
        manageWorkflow(workflow, null);
    }

    @Override
    public void manageWorkflow(WORKFLOW_ENUM workflow, Bundle params) {
        Fragment fragment = null;
        String fragmentTag = null;
        Bundle bundle = new Bundle();

        if(params != null){
            bundle = params;
        }

        int resIdLayout = -1;
        this.workflow = workflow;
        switch (workflow) {
            case WORKFLOW_REG_WEL:{
                fragment = new WelcomeFragment();
                resIdLayout = R.layout.fragment_welcome;
                fragmentTag = WelcomeFragment.FRAGMENT_TAG;
                setTitle("Create Account");
                break;
            }
            case WORKFLOW_REG_NAME:{
                fragment = new RegNameFragment();
                resIdLayout = R.layout.fragment_reg_name;
                fragmentTag = RegNameFragment.FRAGMENT_TAG;
                setTitle("Personal Data");
                break;
            }
            case WORKFLOW_REG_MAIL:{
                fragment = new RegMailPassFragment();
                resIdLayout = R.layout.fragment_reg_mail_pass;
                fragmentTag = RegMailPassFragment.FRAGMENT_TAG;
                setTitle("Authentication Data");
                break;
            }
            case WORKFLOW_REG_GENERAL:{
                fragment = new RegGeneralFragment();
                resIdLayout = R.layout.fragment_reg_general;
                fragmentTag = RegGeneralFragment.FRAGMENT_TAG;
                setTitle("General Informations");
                break;
            }
            case WORKFLOW_REG_BIO:{
                fragment = new RegBioFragment();
                resIdLayout = R.layout.fragment_reg_bio;
                fragmentTag = RegBioFragment.FRAGMENT_TAG;
                setTitle("Biography");
                break;
            }
            case WORKFLOW_REG_PHOTO:{
                fragment = new RegPhotoFragment();
                resIdLayout = R.layout.fragment_reg_photo;
                fragmentTag = RegPhotoFragment.FRAGMENT_TAG;
                setTitle("Profile Picture");
                break;
            }
            case WORKFLOW_REG_SKILLS:{
                fragment = new RegSkillFragment();
                resIdLayout = R.layout.fragment_reg_skill;
                fragmentTag = RegSkillFragment.FRAGMENT_TAG;
                setTitle("Skills");
                break;
            }

            case WORKFLOW_REG_DONE:{
                fragment = new RegDoneFragment();
                resIdLayout = R.layout.fragment_reg_done;
                fragmentTag = RegDoneFragment.FRAGMENT_TAG;
                setTitle("Account Complete");
                break;
            }
        }

        bundle.putInt(MusicianFragment.RES_LAYOUT_ID_PARAM_NAME, resIdLayout);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_registration, fragment, fragmentTag)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private static final int PERMISSION_REQUEST_CODE = 200;
    private boolean checkPermission() {

        return ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                //alertBuilder.setTitle(getString(R.string.permission_necessary));
                //alertBuilder.setMessage(R.string.storage_permission_is_encessary_to_wrote_event);
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainApplication.getInstance().getCurrentActivity(), new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(MainApplication.getInstance().getCurrentActivity(), new String[]{WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            openActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults.length > 0) {

                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }
                if (flag) {
                    openActivity();
                } else {
                    finish();
                }

            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openActivity() {
        //add your further process after giving permission or to download images from remote server.
    }
}
