package me.android.awm.musicianapp;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.bean.NotificationBean;
import me.android.awm.musicianapp.bean.UserBean;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

public class PortalActivity extends AppCompatActivity {
    public static String TAG = "PortalActivity";
    private BottomNavigationView mainNav;
    private FrameLayout mainFrame;
    private String actualFragment = "";

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private ProfileFragment profileFragment;
    private MessageFragment messageFragment;
    private ReqFriendFragment reqFriendFragment;

    private TextView textCartItemCount;
    int mCartItemCount = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().setCurrentActivity(this);
        setContentView(R.layout.activity_portal);
        mainFrame = findViewById(R.id.main_frame);
        mainNav =  findViewById(R.id.main_nav);


        homeFragment = new HomeFragment();
        notificationFragment = new NotificationFragment();

        try {
            MusicianServerApi.getMusician(MainApplication.getInstance().getCurrentActivity(), new HttpManager.HttpManagerCallback() {
                @Override
                public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                    JSONObject json = new JSONObject(response);
                    if(json.has("error")){
                        Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                "Server error", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    UserBean fm = null;
                    try {
                        fm = new UserBean(UserPrefHelper.getLoggedUser());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    json.remove("img");
                    json.put("img", fm.getImg());
                    json.remove("server_img");
                    json.put("server_img", fm.getServer_img());

                    UserPrefHelper.setLoggedUser(json.toString());

                    try {
                        profileFragment = new ProfileFragment(new UserBean(UserPrefHelper.getLoggedUser()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },UserPrefHelper.getCurrentUser().getId());
        } catch (Exception e) {
            e.printStackTrace();
        }



        messageFragment = new MessageFragment();
        reqFriendFragment = new ReqFriendFragment();


        setFragment(homeFragment, homeFragment.FRAGMENT_TAG);

        mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        setFragment(homeFragment, homeFragment.FRAGMENT_TAG);
                        return true;

                    case R.id.nav_notification:
                        setFragment(notificationFragment, notificationFragment.FRAGMENT_TAG);
                        return true;

                    case R.id.nav_profile:
                        setFragment(profileFragment, profileFragment.FRAGMENT_TAG);
                        return true;

                    case R.id.nav_messages:
                        setFragment(messageFragment, messageFragment.FRAGMENT_TAG);
                        return true;

                    case R.id.nav_req_friend:
                        setFragment(reqFriendFragment, reqFriendFragment.FRAGMENT_TAG);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_general:
                View v = findViewById(R.id.menu_general);
                PopupMenu pm = new PopupMenu(this, v);
                pm.getMenuInflater().inflate(R.menu.menu_general, pm.getMenu());
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(getApplicationContext(), String.valueOf(item.getTitle()), Toast.LENGTH_SHORT).show();
                        switch (item.getItemId())   {
                            case R.id.menu_band_list:
                                BandListFragment bandListFragment = new BandListFragment();
                                setFragment(bandListFragment, bandListFragment.FRAGMENT_TAG);
                                break;

                            case R.id.menu_exit:
                                logout();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                }); pm.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem search_item = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) search_item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchFragment searchFragment = new SearchFragment();
                setFragment(searchFragment, searchFragment.FRAGMENT_TAG, query,"query");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void setFragment(Fragment fragment, String tag){
        setFragment(fragment, tag, "","");
    }
    public void setFragment(Fragment fragment, String tag, String data, String key){
        Bundle arguments = new Bundle();
        arguments.putString( key , data);
        fragment.setArguments(arguments);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    private void logout(){
        new AlertDialog.Builder(this)
                .setTitle("Log out")
                .setMessage("Are you sure you want log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        UserPrefHelper.userLogOut();
                        Intent intent = new Intent(PortalActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if ((keyCode == KeyEvent.KEYCODE_BACK)) { //stop your music here
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.main_frame);
            if(f instanceof HomeFragment)
                onBackPressed_portal();
            else if(//f instanceof ProfileFragment ||
                    f instanceof ReqFriendFragment ||
                    f instanceof NotificationFragment ||
                    f instanceof MessageFragment){
                for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                    getSupportFragmentManager().popBackStack();
                }
                homeFragment = new HomeFragment();
                setFragment(homeFragment, homeFragment.FRAGMENT_TAG);
                mainNav.setSelectedItemId(R.id.nav_home);
            }
            else {
                getSupportFragmentManager().popBackStack();
            }
            return true;
        }
        else
        {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void onBackPressed_portal() {
        new AlertDialog.Builder(this)
                .setTitle("Exit app")
                .setMessage("Are you sure you want exit from the app?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}
