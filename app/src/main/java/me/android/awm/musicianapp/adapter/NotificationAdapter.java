package me.android.awm.musicianapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.android.awm.musicianapp.BandFragment;
import me.android.awm.musicianapp.MainApplication;
import me.android.awm.musicianapp.PortalActivity;
import me.android.awm.musicianapp.PostFragment;
import me.android.awm.musicianapp.ProfileFragment;
import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.bean.NotificationBean;
import me.android.awm.musicianapp.bean.UserBean;

public class NotificationAdapter extends ArrayAdapter {
    private Context context;
    private List<NotificationBean> notifications;

    public NotificationAdapter(Context context, List<NotificationBean> notifications) {
        super(context, R.layout.row_notification, notifications);
        this.context = context;
        this.notifications = notifications;
    }

    static class ViewHolder{
        public CircleImageView pic_user;
        public TextView name_notification;
        public TextView date_notif;
        public ImageView icon_type;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        final NotificationBean i = notifications.get(position);
        View v = null;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            v = layoutInflater.inflate(R.layout.row_notification, null);
            viewHolder = new ViewHolder();
            viewHolder.pic_user = (CircleImageView) v.findViewById(R.id.pic_user);
            viewHolder.name_notification = (TextView) v.findViewById(R.id.name_notification);
            viewHolder.date_notif = (TextView) v.findViewById(R.id.date_notif);
            viewHolder.icon_type = (ImageView) v.findViewById(R.id.icon_type);
            v.setTag(viewHolder);
        }
        else{
            v = convertView;
            viewHolder = (ViewHolder) v.getTag();
        }
        viewHolder.pic_user.setImageURI(Uri.fromFile(new File(i.getImg())));
        if(i.getType().equals("comment")) {
            viewHolder.name_notification.setText(i.getFname() + " " + i.getLname() + " has commented your post");
            viewHolder.icon_type.setImageResource(R.drawable.ic_comment_normal);
        }
        else if(i.getType().equals("vote")) {
            viewHolder.name_notification.setText(i.getFname() + " " + i.getLname() + " has voted your post");
            if (i.getVote().equals("like")){
                viewHolder.icon_type.setImageResource(R.drawable.ic_like_normal);
            }
            else{
                viewHolder.icon_type.setImageResource(R.drawable.ic_dislike_normal);
            }
        }
        else {
            viewHolder.name_notification.setText(i.getFname() + " " + i.getLname() +
                    " is a candidate for " + i.getBand().getName_band() + " band");
            viewHolder.icon_type.setVisibility(View.GONE);
        }
        viewHolder.date_notif.setText(i.getDate());

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i.getType().equals("comment") || i.getType().equals("vote")){
                    try {
                        PostFragment postFragment = new PostFragment();
                        Bundle arguments = new Bundle();
                        arguments.putString( "post" , i.getPost().getPostBeanJsonString());
                        postFragment.setArguments(arguments);
                        FragmentTransaction fragmentTransaction = ((PortalActivity) MainApplication.getInstance().
                                getCurrentActivity()).getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_frame,postFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    BandFragment bandFragment = new BandFragment(i.getBand());

                    FragmentTransaction fragmentTransaction = ((PortalActivity) MainApplication.getInstance().
                            getCurrentActivity()).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame,bandFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                if(notifications.size()==1){
                    PortalActivity portal_activity = (PortalActivity) MainApplication.getInstance().getCurrentActivity();
                    Menu menu = portal_activity.mainNav.getMenu();
                    menu.findItem(R.id.nav_notification).setIcon(R.drawable.ic_notifications);
                }
            }
        });
        return v;
    }


}
