package me.android.awm.musicianapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.bean.CommentBean;
import me.android.awm.musicianapp.bean.NotificationBean;

public class CommentAdapter extends ArrayAdapter {
    private Context context;
    private List<CommentBean> comments;

    public CommentAdapter(Context context, List<CommentBean> comments){
        super(context, R.layout.row_comment, comments);
        this.context = context;
        this.comments = comments;
    }

    static class ViewHolder{
        public CircleImageView pic_musician_comment;
        public TextView username_user_comment;
        public TextView date_comment;
        public TextView comment_txt;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        ViewHolder viewHolder = null;
        CommentBean i = comments.get(position);
        View v = null;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            v = layoutInflater.inflate(R.layout.row_comment, null);
            viewHolder = new ViewHolder();
            viewHolder.pic_musician_comment = v.findViewById(R.id.pic_musician_comment);
            viewHolder.username_user_comment =  v.findViewById(R.id.username_user_comment);
            viewHolder.date_comment = v.findViewById(R.id.date_comment);
            viewHolder.comment_txt = v.findViewById(R.id.comment_txt);
            v.setTag(viewHolder);
        }
        else{
            v = convertView;
            viewHolder = (ViewHolder) v.getTag();
        }

        viewHolder.pic_musician_comment.setImageURI(Uri.fromFile(new File(i.getMusician_img())));
        viewHolder.username_user_comment.setText(i.getUsername());
        viewHolder.date_comment.setText(i.getPub_date());
        System.out.println(i.getComment_text());

        if(i.getComment_text().contains("#")) {
            String[] words = i.getComment_text().split(" ");
            SpannableString ss = new SpannableString(i.getComment_text());

            for(final String word: words) {
                if(word.startsWith("#")){
                    ClickableSpan cs = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            System.out.println("OUSUS");
                        }
                    };
                    int startingPosition = i.getComment_text().indexOf(word);
                    int endingPosition = startingPosition + word.length();
                    ss.setSpan(cs,startingPosition,endingPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            viewHolder.comment_txt.setText(ss);
            viewHolder.comment_txt.setMovementMethod(LinkMovementMethod.getInstance());

        }
        else
            viewHolder.comment_txt.setText(i.getComment_text());

        return v;
    }
}
