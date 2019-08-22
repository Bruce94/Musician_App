package me.android.awm.musicianapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.bean.ChatBean;
import me.android.awm.musicianapp.bean.MessageBean;

public class MessageAdapter extends ArrayAdapter {
    private Context context;
    private List<MessageBean> messages;

    public MessageAdapter(Context context, List<MessageBean> messages) {
        super(context, R.layout.row_message, messages);
        this.context = context;
        this.messages = messages;
    }

    static class ViewHolder{
        public TextView text_message;
        public TextView date_message;
        public LinearLayout message_layout_color;
        public LinearLayout message_layout_gravity;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final MessageBean i = messages.get(position);
        View v = null;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            v = layoutInflater.inflate(R.layout.row_message, null);
            viewHolder = new ViewHolder();
            viewHolder.text_message = v.findViewById(R.id.text_message);
            viewHolder.message_layout_color = v.findViewById(R.id.message_layout_color);
            viewHolder.message_layout_gravity = v.findViewById(R.id.message_layout_gravity);
            viewHolder.date_message = v.findViewById(R.id.date_message);
            v.setTag(viewHolder);
        }
        else{
            v = convertView;
            viewHolder = (ViewHolder) v.getTag();
        }
        viewHolder.text_message.setText(i.getText());
        viewHolder.date_message.setText(i.getDate());
        if(i.isSender()){
            viewHolder.message_layout_gravity.setGravity(Gravity.RIGHT);
            viewHolder.message_layout_color.setBackgroundColor(context.getResources().
                    getColor(R.color.colorMusician));
            viewHolder.text_message.setTextColor(context.getResources().
                    getColor(R.color.colorMusicianTxt));
        }
        else{
            viewHolder.message_layout_gravity.setGravity(Gravity.LEFT);
            viewHolder.message_layout_color.setBackgroundColor(context.getResources().
                    getColor(R.color.colorFriend));
            viewHolder.text_message.setTextColor(context.getResources().
                    getColor(R.color.colorFriendTxt));

        }
        viewHolder.date_message.setText(i.getDate());
        return v;
    }
}
