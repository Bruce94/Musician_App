package me.android.awm.musicianapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.android.awm.musicianapp.MainApplication;
import me.android.awm.musicianapp.PortalActivity;
import me.android.awm.musicianapp.ProfileFragment;
import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.bean.SkillInfoBean;
import me.android.awm.musicianapp.bean.UserBean;

public class FriendReqAdapter extends ArrayAdapter {
    private Context context;
    private List<UserBean> users;

    public FriendReqAdapter(Context context, List<UserBean> users) {
        super(context, R.layout.row_friendship_request, users);
        this.context = context;
        this.users = users;
    }

    static class ViewHolder{
        public CircleImageView pic_friend_req;
        public TextView name_user;
        public TextView username_user;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        final UserBean i = users.get(position);
        View v = null;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            v = layoutInflater.inflate(R.layout.row_friendship_request, null);
            viewHolder = new ViewHolder();
            viewHolder.pic_friend_req = (CircleImageView) v.findViewById(R.id.pic_friend_req);
            viewHolder.name_user = (TextView) v.findViewById(R.id.name_user);
            viewHolder.username_user = (TextView) v.findViewById(R.id.username_user);
            v.setTag(viewHolder);
        }
        else{
            v = convertView;
            viewHolder = (ViewHolder) v.getTag();
        }
        viewHolder.pic_friend_req.setImageURI(Uri.fromFile(new File(i.getImg())));
        viewHolder.name_user.setText(i.getFname()+" "+i.getLname());
        viewHolder.username_user.setText("Username: "+ i.getUsername());

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment profileFragment = new ProfileFragment(i);

                FragmentTransaction fragmentTransaction = ((PortalActivity) MainApplication.getInstance().
                        getCurrentActivity()).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,profileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return v;
    }
}
