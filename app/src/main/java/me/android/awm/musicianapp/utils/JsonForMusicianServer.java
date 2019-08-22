package me.android.awm.musicianapp.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonForMusicianServer {
    /**
     * json string that contains email and password for login user
     * @param log_usr
     * @param pwd
     * @return
     * @throws JSONException
     */
    public static JSONObject asJsonForServerLogin(String log_usr, String pwd) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", log_usr);
        jsonObject.put("pwd", pwd);
        return jsonObject;
    }

    public static JSONObject asJsonServerCheckUsername(String username) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        return jsonObject;
    }

    public static JSONObject getVoteJson(int post_id, int vote) throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", UserPrefHelper.getCurrentUser().getId());
        jsonObject.put("post_id", post_id);
        jsonObject.put("vote", vote);
        return jsonObject;
    }

    public static JSONObject getNewPostJson(String post_text,
                                            String post_image) throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", UserPrefHelper.getCurrentUser().getId());
        jsonObject.put("post_text", post_text);
        jsonObject.put("img", post_image);
        return jsonObject;
    }

    public static JSONObject getJsontoDeletePost(int post_id) throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", UserPrefHelper.getCurrentUser().getId());
        jsonObject.put("post_id", post_id);
        return jsonObject;
    }

    public static JSONObject getJsontoSendComment(String comment_text) throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", UserPrefHelper.getCurrentUser().getId());
        jsonObject.put("comment_text", comment_text);
        return jsonObject;
    }

    public static JSONObject getJsonForSetFriendship(String status) throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", UserPrefHelper.getCurrentUser().getId());
        jsonObject.put("status", status);
        return jsonObject;
    }

    public static JSONObject getJsonForEndorseSkill(String skill_name) throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", UserPrefHelper.getCurrentUser().getId());
        jsonObject.put("skill_name", skill_name);
        return jsonObject;
    }

    public static JSONObject getJsonForSendMessage(String message) throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", UserPrefHelper.getCurrentUser().getId());
        jsonObject.put("message", message);
        return jsonObject;
    }

    public static JSONObject getJsonForUserCandidate(List<String> check_skill) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", UserPrefHelper.getCurrentUser().getId());
        JSONArray jsonArray = new JSONArray();
        for (String cs: check_skill) {
            jsonArray.put(cs);
        }
        jsonObject.put("check_skill", jsonArray);
        return jsonObject;
    }

    public static JSONObject getJsonForMembersBand(boolean candidates) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", UserPrefHelper.getCurrentUser().getId());
        jsonObject.put("candidates", candidates);
        return jsonObject;
    }

    public static JSONObject getJsonForRemCand(int user_id) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", user_id);
        return jsonObject;
    }

    public static JSONObject getJsonForTagPost(String tag) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", UserPrefHelper.getCurrentUser().getId());
        jsonObject.put("tag", tag.substring(1));
        return jsonObject;
    }

    public static JSONObject getJsonForAccCand(int user_id,
                                               List<String> check_skill_conf) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", user_id);
        JSONArray jsonArray = new JSONArray();
        for (String cs: check_skill_conf) {
            jsonArray.put(cs);
        }
        jsonObject.put("check_skill_conf", jsonArray);
        return jsonObject;
    }

    public static JSONObject getJsonForBandList(String query,
                                                List<String> skills) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("query", query);
        JSONArray jsonArray = new JSONArray();
        for (String cs: skills) {
            jsonArray.put(cs);
        }
        jsonObject.put("checked_skills", jsonArray);
        return jsonObject;
    }
}
