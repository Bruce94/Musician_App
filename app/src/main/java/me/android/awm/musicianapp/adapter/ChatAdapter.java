package me.android.awm.musicianapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.android.awm.musicianapp.ChatFragment;
import me.android.awm.musicianapp.MainApplication;
import me.android.awm.musicianapp.PortalActivity;
import me.android.awm.musicianapp.ProfileFragment;
import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.bean.ChatBean;
import me.android.awm.musicianapp.bean.UserBean;

public class ChatAdapter extends ArrayAdapter {
    private Context context;
    private List<ChatBean> chats;

    public ChatAdapter(Context context, List<ChatBean> chats) {
        super(context, R.layout.row_chat, chats);
        this.context = context;
        this.chats = chats;
    }

    static class ViewHolder{
        public CircleImageView pic_user_chat;
        public TextView name_user;
        public TextView last_message;
        public TextView date_message;
        public LinearLayout chat_layout;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        final ChatBean i = chats.get(position);
        View v = null;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            v = layoutInflater.inflate(R.layout.row_chat, null);
            viewHolder = new ViewHolder();
            viewHolder.pic_user_chat = v.findViewById(R.id.pic_user_chat);
            viewHolder.name_user = v.findViewById(R.id.name_user);
            viewHolder.last_message = v.findViewById(R.id.last_message);
            viewHolder.date_message = v.findViewById(R.id.date_message);
            viewHolder.chat_layout = v.findViewById(R.id.chat_layout);
            v.setTag(viewHolder);
        }
        else{
            v = convertView;
            viewHolder = (ViewHolder) v.getTag();
        }
        viewHolder.pic_user_chat.setImageURI(Uri.fromFile(new File(i.getMusician_img())));
        viewHolder.name_user.setText(i.getFname()+" "+i.getLname());
        viewHolder.last_message.setText(i.getMessage());
        viewHolder.date_message.setText(i.getDate());

        if(!i.isSeen())
            viewHolder.chat_layout.setBackgroundColor(context.getResources().
                    getColor(R.color.colorFriend));
        else
            viewHolder.chat_layout.setBackgroundColor(context.getResources().
                    getColor(R.color.colorWhiteTrasparent));

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatFragment chatFragment = new ChatFragment(i.getUser_id(),
                        i.getFname()+" "+i.getLname(), i.getMusician_img());

                FragmentTransaction fragmentTransaction = ((PortalActivity) MainApplication.getInstance().
                        getCurrentActivity()).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,chatFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return v;
    }
}
