package me.android.awm.musicianapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.android.awm.musicianapp.adapter.CommentAdapter;
import me.android.awm.musicianapp.adapter.FriendReqAdapter;
import me.android.awm.musicianapp.adapter.PostAdapter;
import me.android.awm.musicianapp.bean.CommentBean;
import me.android.awm.musicianapp.bean.PostBean;
import me.android.awm.musicianapp.bean.SkillInfoBean;
import me.android.awm.musicianapp.bean.UserBean;
import me.android.awm.musicianapp.net.DownloadTask;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

import static android.content.Context.MODE_PRIVATE;

public class PostFragment extends Fragment {

    private PostBean post;
    private CircleImageView pic_profile_post;
    private TextView name_user_post, date_post;
    private TextView n_like_text, n_dislike_text, n_comments_text, post_text;
    private ImageView post_image;
    private LinearLayout like_btn, dislike_btn;
    private LinearLayout list_comments;
    private List<CommentBean> comments = new LinkedList<CommentBean>();
    private Button send_comment_btn;
    private EditText comment_txt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle arguments = getArguments();
        try {
            post = new PostBean(arguments.getString("post"));
            try {
                MusicianServerApi.commentsPost(MainApplication.getInstance().getCurrentActivity(), new HttpManager.HttpManagerCallback() {
                    @Override
                    public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                        JSONObject json = new JSONObject(response);
                        if(json.has("data")){
                            JSONArray array = json.getJSONArray("data");
                            for(int i = 0; i < array.length(); i++){
                                try {
                                    CommentBean c = new CommentBean(array.get(i).toString());
                                    download_prof_images(c);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else{
                            Toast.makeText(MainApplication.getInstance().getCurrentActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, post.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        pic_profile_post = getActivity().findViewById(R.id.pic_profile_post);
        name_user_post = getActivity().findViewById(R.id.name_user_post);
        date_post = getActivity().findViewById(R.id.date_post);
        n_like_text = getActivity().findViewById(R.id.n_like_text);
        n_dislike_text = getActivity().findViewById(R.id.n_dislike_text);
        n_comments_text = getActivity().findViewById(R.id.n_comments_text);
        post_text = getActivity().findViewById(R.id.post_text);
        post_image = getActivity().findViewById(R.id.post_image);
        like_btn = getActivity().findViewById(R.id.like_btn);
        dislike_btn = getActivity().findViewById(R.id.dislike_btn);
        list_comments = getActivity().findViewById(R.id.list_comments);
        send_comment_btn = getActivity().findViewById(R.id.send_comment_btn);
        comment_txt = getActivity().findViewById(R.id.comment_txt);

        pic_profile_post.setImageURI(Uri.fromFile(new File(post.getMusician_img())));

        if(post.getPost_text().contains("#")) {
            String[] words = post.getPost_text().split(" ");
            SpannableString ss = new SpannableString(post.getPost_text());

            for(final String word: words) {
                if(word.startsWith("#")){
                    ClickableSpan cs = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            PostAdapter.go_to_tag_post(word);
                        }
                    };
                    int startingPosition = post.getPost_text().indexOf(word);
                    int endingPosition = startingPosition + word.length();
                    ss.setSpan(cs,startingPosition,endingPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            post_text.setText(ss);
            post_text.setMovementMethod(LinkMovementMethod.getInstance());

        }
        else
            post_text.setText(post.getPost_text());
        name_user_post.setText(post.getMusician_fname()+" "+post.getMusician_lname());
        date_post.setText(post.getPub_date());

        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);
                call_vote(post.getId(),1, v);
            }
        });

        dislike_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);
                call_vote(post.getId(),2,v);
            }
        });

        set_post_data();

        comment_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    send_comment_btn.setEnabled(false);
                } else {
                    send_comment_btn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        send_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_comment_btn.setEnabled(false);
                try {
                    MusicianServerApi.sendCommentPostUrl(getActivity(), new HttpManager.HttpManagerCallback() {
                        @Override
                        public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                            JSONObject json = new JSONObject(response);
                            if(json.has("data")){
                                JSONObject obj = json.getJSONObject("data");
                                try {
                                    post.setN_comments(post.getN_comments()+1);
                                    set_post_data();
                                    CommentBean c = new CommentBean(obj.toString());
                                    download_prof_images(c);
                                    if(post.getMusician_id() != UserPrefHelper.getCurrentUser().getId()) {
                                        String mess = post.getMusician_id() + "";
                                        MainApplication.socket.emit("new general notification", mess);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                Toast.makeText(MainApplication.getInstance().getCurrentActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, post.getId(), comment_txt.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                comment_txt.setText("");
            }
        });
    }


    static class ViewHolderComment {
        public CircleImageView pic_musician_comment;
        public TextView username_user_comment;
        public TextView date_comment;
        public TextView comment_txt;
    }

    public void insert_comment(CommentBean c){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.row_comment, null);

        PostFragment.ViewHolderComment viewHolder = new PostFragment.ViewHolderComment();
        viewHolder.pic_musician_comment = v.findViewById(R.id.pic_musician_comment);
        viewHolder.username_user_comment = v.findViewById(R.id.username_user_comment);
        viewHolder.date_comment = v.findViewById(R.id.date_comment);
        viewHolder.comment_txt = v.findViewById(R.id.comment_txt);
        viewHolder.pic_musician_comment.setImageURI(Uri.fromFile(new File(c.getMusician_img())));
        viewHolder.username_user_comment.setText(c.getUsername());
        viewHolder.date_comment.setText(c.getPub_date());

        if(c.getComment_text().contains("#")) {
            String[] words = c.getComment_text().split(" ");
            SpannableString ss = new SpannableString(c.getComment_text());
            for(final String word: words) {
                if(word.startsWith("#")){
                    ClickableSpan cs = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            PostAdapter.go_to_tag_post(word);
                        }
                    };
                    int startingPosition = c.getComment_text().indexOf(word);
                    int endingPosition = startingPosition + word.length();
                    ss.setSpan(cs,startingPosition,endingPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            viewHolder.comment_txt.setText(ss);
            viewHolder.comment_txt.setMovementMethod(LinkMovementMethod.getInstance());

        }
        else
            viewHolder.comment_txt.setText(c.getComment_text());
        v.setTag(viewHolder);
        list_comments.addView(v);
    }

    private void download_prof_images(final CommentBean comment){
        try {
            File file = new File(MainApplication.getInstance().getCurrentActivity().getApplicationContext().getDir("Images",MODE_PRIVATE),
                    comment.getUsername()+".jpg");
            if(file.exists()){
                comment.setMusician_img(Uri.parse(file.getAbsolutePath()).toString());
                comments.add(comment);
                insert_comment(comment);
            }
            else {
                MusicianServerApi.downloadImage(MainApplication.getInstance().getCurrentActivity(), new DownloadTask.DownloadTaskCallback() {
                    @Override
                    public void downloadTaskCallbackResult(Uri imageInternalUri) throws JSONException {
                        comment.setMusician_img(imageInternalUri.toString());
                        comments.add(comment);
                        insert_comment(comment);
                    }
                }, comment.getMusician_img(), comment.getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void call_vote(int post_id, int vote, final View v){
        try {
            MusicianServerApi.vote_post(MainApplication.getInstance().getCurrentActivity(),
                    new HttpManager.HttpManagerCallback() {
                        @Override
                        public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                            JSONObject json = new JSONObject(response);
                            if(json.has("post")){
                                v.setEnabled(true);
                                JSONObject post_j = (JSONObject) json.get("post");
                                try {
                                    PostBean mod_post = new PostBean(post_j.toString());

                                    post.setN_like(mod_post.getN_like());
                                    post.setN_dislike(mod_post.getN_dislike());
                                    post.setVote(mod_post.getVote());
                                    set_post_data();

                                    if(post.getMusician_id() != UserPrefHelper.getCurrentUser().getId()) {
                                        String mess = post.getMusician_id() + "";
                                        MainApplication.socket.emit("new general notification", mess);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                Toast.makeText(MainApplication.getInstance().getCurrentActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, post_id, vote);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void set_post_data(){

        final TextView like_txt = (TextView) like_btn.getChildAt(1);
        final ImageView like_img = (ImageView) like_btn.getChildAt(0);
        final TextView dislike_txt = (TextView) dislike_btn.getChildAt(1);
        final ImageView dislike_img = (ImageView) dislike_btn.getChildAt(0);

        if(post.getVote() == 1){
            like_img.setImageResource(R.drawable.ic_like_blue);
            like_txt.setTextColor(MainApplication.getInstance().getResources().getColor(R.color.colorLike));
            dislike_img.setImageResource(R.drawable.ic_dislike_normal);
            dislike_txt.setTextColor(MainApplication.getInstance().getResources().getColor(R.color.colorPrimary));
        }
        else if(post.getVote() == 2){
            like_img.setImageResource(R.drawable.ic_like_normal);
            like_txt.setTextColor(MainApplication.getInstance().getResources().getColor(R.color.colorPrimary));
            dislike_img.setImageResource(R.drawable.ic_dislike_red);
            dislike_txt.setTextColor(MainApplication.getInstance().getResources().getColor(R.color.colorDislike));
        }
        else{
            like_img.setImageResource(R.drawable.ic_like_normal);
            like_txt.setTextColor(MainApplication.getInstance().getResources().getColor(R.color.colorPrimary));
            dislike_img.setImageResource(R.drawable.ic_dislike_normal);
            dislike_txt.setTextColor(MainApplication.getInstance().getResources().getColor(R.color.colorPrimary));
        }

        if(!post.getPost_image().equals("")) {
            post_image.setImageURI(Uri.fromFile(new File(post.getPost_image())));
            post_image.setVisibility(View.VISIBLE);
        }
        if (post.getN_like() > 0){
            n_like_text.setText(post.getN_like()+" Like");
            n_like_text.setVisibility(View.VISIBLE);
        }
        else{
            n_like_text.setVisibility(View.GONE);
        }
        if (post.getN_dislike() > 0){
            n_dislike_text.setText(post.getN_dislike()+" Dislike");
            n_dislike_text.setVisibility(View.VISIBLE);
        }
        else{
            n_dislike_text.setVisibility(View.GONE);
        }
        if (post.getN_comments() > 0){
            n_comments_text.setText(post.getN_comments()+" Comment");
            n_comments_text.setVisibility(View.VISIBLE);
        }
    }

}
