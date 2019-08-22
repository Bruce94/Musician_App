package me.android.awm.musicianapp;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.android.awm.musicianapp.adapter.ChatAdapter;
import me.android.awm.musicianapp.adapter.MessageAdapter;
import me.android.awm.musicianapp.bean.ChatBean;
import me.android.awm.musicianapp.bean.MessageBean;
import me.android.awm.musicianapp.bean.UserBean;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.net.api.MusicianServerApi;
import me.android.awm.musicianapp.utils.UserPrefHelper;

@SuppressLint("ValidFragment")
public class ChatFragment  extends Fragment {
    public static String FRAGMENT_TAG = "ChatFragment";

    private MessageAdapter adapter;
    private List<MessageBean> messages = new LinkedList<MessageBean>();
    private ListView list_messages;
    private TextView name_user;
    private CircleImageView pic_user_chat;
    private String names_user, img_url;
    private Button send_message_btn;
    private EditText edit_message_txt;
    private int user_id;


    @SuppressLint("ValidFragment")
    public ChatFragment(int user_id, String names_user, String img_url) {
        this.names_user = names_user;
        this.img_url = img_url;
        this.user_id = user_id;
        get_messages();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        list_messages = getActivity().findViewById(R.id.list_messages);
        name_user = getActivity().findViewById(R.id.name_user);
        pic_user_chat = getActivity().findViewById(R.id.pic_user_chat);
        send_message_btn = getActivity().findViewById(R.id.send_message_btn);
        edit_message_txt = getActivity().findViewById(R.id.edit_message_txt);

        pic_user_chat.setImageURI(Uri.fromFile(new File(img_url)));
        name_user.setText(names_user);
        adapter = new MessageAdapter(getActivity(), messages);
        list_messages.setAdapter(adapter);

        edit_message_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    send_message_btn.setEnabled(false);
                } else {
                    send_message_btn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        MainApplication.socket.on("message arrived", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONArray data;
                    if(args[0].getClass() == String.class){
                        String[] parts = args[0].toString().split(",");
                        data = new JSONArray();
                        for(String part:parts){
                            data.put(part);
                        }
                    }
                    else
                        data = new JSONArray(args[0].toString());
                    if(data.getInt(0)==UserPrefHelper.getCurrentUser().getId()){
                        if (data.getInt(1) == user_id) {
                            get_messages();
                            System.out.println("oooo");
                            String mess = data.getInt(0)+","+data.getInt(1)
                                    +","+data.getString(2)+","+data.getString(3)
                                    +","+data.getString(4);
                            MainApplication.socket.emit("message not seen", mess);
                        }
                    }
                    else if(data.getInt(1) == UserPrefHelper.getCurrentUser().getId()){
                        if (data.getInt(0) == user_id) {
                            get_messages();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        send_message_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_message();
            }
        });
    }

    private void get_messages(){
        try {
            MusicianServerApi.getMessages(MainApplication.getInstance().getCurrentActivity(),
                    new HttpManager.HttpManagerCallback() {
                        @Override
                        public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                            JSONObject json = new JSONObject(response);
                            if(json.has("messages")){
                                JSONArray array = json.getJSONArray("messages");
                                for(int i = 0; i < array.length(); i++){
                                    try {
                                        MessageBean m = new MessageBean(array.get(i).toString());
                                        messages.add(m);
                                        adapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            else{
                                Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                        "Server Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },user_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send_message(){
        try {
            MusicianServerApi.sendMessage(getActivity(), new HttpManager.HttpManagerCallback() {
                @Override
                public void httpManagerCallbackResult(String response, boolean esito) throws JSONException {
                    JSONObject json = new JSONObject(response);
                    if(json.has("text")){
                        try {
                            MessageBean m = new MessageBean(json.toString());
                            messages.add(m);
                            adapter.notifyDataSetChanged();
                            UserBean logged = UserPrefHelper.getCurrentUser();
                            String mess = logged.getId()+","+user_id+","+m.getText()
                                    +","+logged.getFname()+" "+logged.getLname()
                                    +","+ logged.getServer_img();
                            MainApplication.socket.emit("message sent",(Object) mess);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    else{
                        Toast.makeText(MainApplication.getInstance().getCurrentActivity(),
                                "Server Error", Toast.LENGTH_SHORT).show();
                    }
                }
            },user_id, edit_message_txt.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        edit_message_txt.setText("");

    }
}
