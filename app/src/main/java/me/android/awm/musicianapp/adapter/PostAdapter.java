package me.android.awm.musicianapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.android.awm.musicianapp.CreatePostFragment;
import me.android.awm.musicianapp.MainApplication;
import me.android.awm.musicianapp.PortalActivity;
import me.android.awm.musicianapp.PostFragment;
import me.android.awm.musicianapp.ProfileFragment;
import me.android.awm.musicianapp.R;
import me.android.awm.musicianapp.TagFragment;
import me.android.awm.musicianapp.bean.NotificationBean;
import me.android.awm.musicianapp.bean.PostBean;
import me.android.awm.musicianapp.bean.UserBean;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

public class PostAdapter extends ArrayAdapter {
    private Context context;
    private List<PostBean> posts;

    public PostAdapter(Context context, List<PostBean> posts) {
        super(context, R.layout.row_post, posts);
        this.context = context;
        this.posts = posts;
    }

    static class ViewHolder{
        public CircleImageView pic_profile_post;
        public TextView name_user_post;
        public TextView date_post;
        public TextView post_text;
        public TextView n_like_text;
        public TextView n_dislike_text;
        public TextView n_comments_text;
        public LinearLayout like_btn;
        public LinearLayout dislike_btn;
        public LinearLayout comment_btn;
        public ImageView post_image;
        public ImageView delete_btn;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        final PostBean i = posts.get(position);
        View v = null;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            v = layoutInflater.inflate(R.layout.row_post, null);
            viewHolder = new PostAdapter.ViewHolder();
            viewHolder.pic_profile_post = v.findViewById(R.id.pic_profile_post);
            viewHolder.name_user_post = v.findViewById(R.id.name_user_post);
            viewHolder.date_post = v.findViewById(R.id.date_post);
            viewHolder.post_image = v.findViewById(R.id.post_image);
            viewHolder.post_text = v.findViewById(R.id.post_text);
            viewHolder.n_like_text = v.findViewById(R.id.n_like_text);
            viewHolder.n_dislike_text = v.findViewById(R.id.n_dislike_text);
            viewHolder.n_comments_text = v.findViewById(R.id.n_comments_text);
            viewHolder.like_btn = v.findViewById(R.id.like_btn);
            viewHolder.dislike_btn = v.findViewById(R.id.dislike_btn);
            viewHolder.comment_btn = v.findViewById(R.id.comment_btn);
            viewHolder.delete_btn = v.findViewById(R.id.delete_btn);

            v.setTag(viewHolder);
        }
        else{
            v = convertView;
            viewHolder = (PostAdapter.ViewHolder) v.getTag();
        }
        viewHolder.pic_profile_post.setImageURI(Uri.fromFile(new File(i.getMusician_img())));
        viewHolder.pic_profile_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goto_musician_profile(i.getMusician_id(), i.getMusician_img());
            }
        });


        if(i.getPost_text().contains("#")) {
            String[] words = i.getPost_text().split(" ");
            SpannableString ss = new SpannableString(i.getPost_text());

            for(final String word: words) {
                if(word.startsWith("#")){
                    ClickableSpan cs = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            go_to_tag_post(word);
                        }
                    };
                    int startingPosition = i.getPost_text().indexOf(word);
                    int endingPosition = startingPosition + word.length();
                    ss.setSpan(cs,startingPosition,endingPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            viewHolder.post_text.setText(ss);
            viewHolder.post_text.setMovementMethod(LinkMovementMethod.getInstance());

        }
        else
            viewHolder.post_text.setText(i.getPost_text());

        viewHolder.name_user_post.setText(i.getMusician_fname()+" "+i.getMusician_lname());
        viewHolder.date_post.setText(i.getPub_date());

        try {
            if(i.getMusician_id() == UserPrefHelper.getCurrentUser().getId()) {
                viewHolder.delete_btn.setVisibility(View.VISIBLE);
                viewHolder.delete_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        call_delete(i.getId());
                    }
                });
            }
            else
                viewHolder.delete_btn.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final TextView like_txt = (TextView) viewHolder.like_btn.getChildAt(1);
        final ImageView like_img = (ImageView) viewHolder.like_btn.getChildAt(0);
        final TextView dislike_txt = (TextView) viewHolder.dislike_btn.getChildAt(1);
        final ImageView dislike_img = (ImageView) viewHolder.dislike_btn.getChildAt(0);
        viewHolder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);
                call_vote(i.getId(),1, v);
            }
        });

        viewHolder.dislike_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);
                call_vote(i.getId(),2,v);
            }
        });

        viewHolder.comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                comment(i);
            }
        });

        if(i.getVote() == 1){
            like_img.setImageResource(R.drawable.ic_like_blue);
            like_txt.setTextColor(MainApplication.getInstance().getResources().getColor(R.color.colorLike));
            dislike_img.setImageResource(R.drawable.ic_dislike_normal);
            dislike_txt.setTextColor(MainApplication.getInstance().getResources().getColor(R.color.colorPrimary));
        }
        else if(i.getVote() == 2){
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
        if(!i.getPost_image().equals("")){
            viewHolder.post_image.setImageURI(Uri.fromFile(new File(i.getPost_image())));
            viewHolder.post_image.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.post_image.setVisibility(View.GONE);
        }
        if (i.getN_like() > 0){
            viewHolder.n_like_text.setText(i.getN_like()+" Like");
            viewHolder.n_like_text.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.n_like_text.setVisibility(View.GONE);
        }
        if (i.getN_dislike() > 0){
            viewHolder.n_dislike_text.setText(i.getN_dislike()+" Dislike");
            viewHolder.n_dislike_text.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.n_dislike_text.setVisibility(View.GONE);
        }
        if (i.getN_comments() > 0){
            viewHolder.n_comments_text.setText(i.getN_comments()+" Comment");
            viewHolder.n_comments_text.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.n_comments_text.setVisibility(View.GONE);
        }
        return v;
    }

    private void call_delete(final int post_id){
        try{
            MusicianServerApi.deletePost(MainApplication.getInstance().getCurrentActivity(),
                    new HttpManager.HttpManagerCallback() {
                        @Override
                        public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                            for(PostBean p : posts){
                                if(p.getId() == post_id){
                                    posts.remove(p);
                                    break;
                                }
                            }
                            notifyDataSetChanged();
                        }
                    }, post_id);
        }catch (Exception e) {
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
                                    PostBean post = new PostBean(post_j.toString());
                                    for(int i = 0; i < posts.size(); i++){
                                        if(posts.get(i).getId() == post.getId()){
                                            post.setPost_image(posts.get(i).getPost_image());
                                            post.setMusician_img(posts.get(i).getMusician_img());
                                            posts.set(i, post);
                                            notifyDataSetChanged();
                                            if(post.getMusician_id() != UserPrefHelper.getCurrentUser().getId()) {
                                                String mess = post.getMusician_id() + "";
                                                MainApplication.socket.emit("new general notification", mess);
                                            }
                                        }
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

    private void comment(PostBean post){
        PostFragment createPostFragment = new PostFragment();
        Bundle arguments = new Bundle();
        try {
            arguments.putString( "post" , post.getPostBeanJsonString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        createPostFragment.setArguments(arguments);

        FragmentTransaction fragmentTransaction = ((PortalActivity) MainApplication.getInstance().
                getCurrentActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,createPostFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static void go_to_tag_post(String tag){
        TagFragment fragment = new TagFragment();
        Bundle arguments = new Bundle();
        arguments.putString( "tag" , tag);
        fragment.setArguments(arguments);

        FragmentTransaction fragmentTransaction = ((PortalActivity) MainApplication.getInstance().
                getCurrentActivity()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void goto_musician_profile(int user_id, final String img){
        try {
            MusicianServerApi.getMusician(context, new HttpManager.HttpManagerCallback() {
                @Override
                public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                    JSONObject json = new JSONObject(response);
                    if(json.has("error")){
                        Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                "Server Error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        UserBean musician = new UserBean(json.toString());
                        musician.setImg(img);
                        ProfileFragment profileFragment = new ProfileFragment(musician);

                        FragmentTransaction fragmentTransaction = ((PortalActivity) MainApplication.getInstance().
                                getCurrentActivity()).getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_frame,profileFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, user_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
