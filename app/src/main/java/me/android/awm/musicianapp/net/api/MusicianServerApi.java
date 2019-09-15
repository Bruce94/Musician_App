package me.android.awm.musicianapp.net.api;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import me.android.awm.musicianapp.net.DownloadTask;
import me.android.awm.musicianapp.net.HttpManager;
import me.android.awm.musicianapp.utils.JsonForMusicianServer;
import me.android.awm.musicianapp.utils.UserPrefHelper;


public class MusicianServerApi {

    //private static String domain = "http://192.168.1.11:8000";
    //private static String domain = "http://10.42.92.74:8000";
    private static String domain = "http://musicianawm.herokuapp.com";
    private static String loginUrl() { return domain+"/login_app/"; }
    private static String getTokenUrl() {return domain+"/get_csrf/";}
    private static String checkUsernameUrl(){return domain+"/check_username/";}
    private static String userRegistrationUrl(){return domain+"/registration_app/";}
    private static String getUserFriendsUrl(int user_id){return domain+"/musician/"+user_id+"/friends/app/";}
    private static String getFrirenshipReqUrl(){return domain+"/portal/friendship_req/app/";}
    private static String getNotificationsUrl(){return domain+"/portal/notifications/app/";}
    private static String searchMusiciansUrl(){return domain+"/portal/search_musician/app/";}
    private static String getUserInfoUrl(int user_id){return domain+"/musician/"+user_id+"/get_user_info/app/";}
    private static String suggestedUrl(int user_id){return domain+"/portal/"+user_id+"/suggested/app/";}
    private static String homePostsUrl(){return domain+"/portal/home_posts/app/";}
    private static String votePostUrl(){return domain+"/portal/vote_post/app/";}
    private static String newPostUrl(){return domain+"/portal/new_post/app/";}
    private static String deletePostUrl(){return domain+"/portal/delete_post/app/";}
    private static String commentsPostUrl(int post_id){return  domain+"/portal/"+post_id+"/comments/app/";}
    private static String sendCommentPostUrl(int post_id){return  domain+"/portal/"+post_id+"/send_comment/app/";}
    private static String setFriendshipUrl(int user_id){return domain+"/musician/"+user_id+"/set_friendship/app/";}
    private static String getUserPostsUrl(int user_id){return domain+"/musician/"+user_id+"/posts/app/";}
    private static String getTagPostsUrl(){return domain+"/portal/tag_posts/app/";}
    private static String endorseSkillUrl(int user_id){return domain+"/musician/"+user_id+"/endorse_skill/app/";}
    private static String getMusicianUrl(int user_id){return domain+"/musician/"+user_id+"/get_musician/app/";}
    private static String getChatsUrl(){return domain+"/musician/get_chats/app/";}
    private static String getMessagesUrl(int user_id){return domain+"/musician/"+user_id+"/get_messages/app/";}
    private static String sendMessageUrl(int user_id){return domain+"/musician/"+user_id+"/send_message/app/";}
    private static String bandListUrl(){return domain+"/musician/bands_list/app/";}
    private static String getBandInfoUrl(int band_id){return domain+"/musician/"+band_id+"/get_band_info/app/";}
    private static String userCandidateBandUrl(int band_id){return domain+"/musician/"+band_id+"/user_candidate_band/app/";}
    private static String userRemoveCandBandUrl(int band_id){return domain+"/musician/"+band_id+"/user_remove_cand_band/app/";}
    private static String userAcceptCandBandUrl(int band_id){return domain+"/musician/"+band_id+"/user_accept_cand_band/app/";}
    private static String getBandMembersUrl(int band_id){return domain+"/musician/"+band_id+"/get_band_members/app/";}
    private static String changeBioUrl(){return domain+"/musician/change_bio/app/";}
    private static String changeInfoUrl(){return domain+"/musician/change_info/app/";}
    private static String changeSkillsUrl(){return domain+"/musician/change_skills/app/";}
    private static String viewULikesUrl(){return domain+"/portal/view_ulikes/app/";}

    public static void loginUser(Context context, HttpManager.HttpManagerCallback callback,
                                 String username, String pwd) throws JSONException {
        System.out.println(JsonForMusicianServer.asJsonForServerLogin(username, pwd));
        HttpManager manager = new HttpManager(context, loginUrl(),
                JsonForMusicianServer.asJsonForServerLogin(username, pwd),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void checkUsername(Context context, HttpManager.HttpManagerCallback callback,
                                     String username) throws JSONException {
        HttpManager manager = new HttpManager(context, checkUsernameUrl(),
                JsonForMusicianServer.asJsonServerCheckUsername(username),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }


    public static void userRegistration(Context context, HttpManager.HttpManagerCallback callback,
                                        JSONObject json) throws JSONException {

        HttpManager manager = new HttpManager(context, userRegistrationUrl(),
                json, HttpManager.OPERATION_TYPE.OPERATION_TYPE_MULTIPART,callback);
        manager.execute();

    }


    public static void getToken(Context context,
                                HttpManager.HttpManagerCallback callback) throws JSONException {
        HttpManager manager = new HttpManager(context, getTokenUrl(), null,
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_GET, callback);
        manager.execute();
    }

    public static void downloadImage(Context context, DownloadTask.DownloadTaskCallback callback,
                                     String img_url, String img_name){
        DownloadTask manager = new DownloadTask(context, domain+img_url,
                callback,img_name);
        manager.execute();
    }

    public static void getUserFriendsUrl(Context context, HttpManager.HttpManagerCallback callback,
                                         int user_id) throws JSONException {
        HttpManager manager = new HttpManager(context, getUserFriendsUrl(user_id), null,
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_GET, callback);
        manager.execute();
    }

    public static void getFrirenshipReq(Context context,
                                        HttpManager.HttpManagerCallback callback) throws Exception {
        HttpManager manager = new HttpManager(context, getFrirenshipReqUrl(),
                UserPrefHelper.getLoggedUsername(),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }


    public static void getNotifications(Context context,
                                        HttpManager.HttpManagerCallback callback) throws Exception {
        HttpManager manager = new HttpManager(context, getNotificationsUrl(),
                UserPrefHelper.getLoggedUsername(),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void searchMusicians(Context context, HttpManager.HttpManagerCallback callback,
                                       JSONObject query) throws Exception {
        HttpManager manager = new HttpManager(context, searchMusiciansUrl(),
                query, HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void getUserInfo(Context context, HttpManager.HttpManagerCallback callback,
                                      int user_id) throws Exception {
        HttpManager manager = new HttpManager(context, getUserInfoUrl(user_id),
                UserPrefHelper.getLoggedUsername(),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void suggested(Context context, HttpManager.HttpManagerCallback callback,
                                 int user_id) throws Exception {
        HttpManager manager = new HttpManager(context, suggestedUrl(user_id),
                null, HttpManager.OPERATION_TYPE.OPERATION_TYPE_GET, callback);
        manager.execute();
    }

    public static void home_posts(Context context,
                                  HttpManager.HttpManagerCallback callback) throws Exception {
        HttpManager manager = new HttpManager(context, homePostsUrl(),
                UserPrefHelper.getLoggedUsername(),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void vote_post(Context context, HttpManager.HttpManagerCallback callback,
                                 int post_id, int vote) throws Exception {
        HttpManager manager = new HttpManager(context, votePostUrl(),
                JsonForMusicianServer.getVoteJson(post_id,vote),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void newPost(Context context, HttpManager.HttpManagerCallback callback,
                               String post_text, String post_image) throws Exception {
        HttpManager manager = new HttpManager(context, newPostUrl(),
                JsonForMusicianServer.getNewPostJson(post_text, post_image),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_MULTIPART, callback);
        manager.execute();
    }

    public static void deletePost(Context context, HttpManager.HttpManagerCallback callback,
                                  int post_id) throws Exception {
        HttpManager manager = new HttpManager(context, deletePostUrl(),
                JsonForMusicianServer.getJsontoDeletePost(post_id),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void commentsPost(Context context, HttpManager.HttpManagerCallback callback,
                                 int post_id) throws Exception {
        HttpManager manager = new HttpManager(context, commentsPostUrl(post_id),
                null, HttpManager.OPERATION_TYPE.OPERATION_TYPE_GET, callback);
        manager.execute();
    }

    public static void sendCommentPostUrl(Context context, HttpManager.HttpManagerCallback callback,
                                          int post_id, String comment_text) throws Exception {
        HttpManager manager = new HttpManager(context, sendCommentPostUrl(post_id),
                JsonForMusicianServer.getJsontoSendComment(comment_text),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void setFriendship(Context context, HttpManager.HttpManagerCallback callback,
                                   int user_id, String status) throws Exception {
        HttpManager manager = new HttpManager(context, setFriendshipUrl(user_id),
                JsonForMusicianServer.getJsonForSetFriendship(status),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void getTagPosts(Context context, HttpManager.HttpManagerCallback callback,
                                         String tag) throws Exception {
        HttpManager manager = new HttpManager(context, getTagPostsUrl(),
                JsonForMusicianServer.getJsonForTagPost(tag),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void getUserPosts(Context context, HttpManager.HttpManagerCallback callback,
                                    int user_id) throws Exception {
        HttpManager manager = new HttpManager(context, getUserPostsUrl(user_id),
                UserPrefHelper.getLoggedUsername(),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void endorseSkill(Context context, HttpManager.HttpManagerCallback callback,
                                    int user_id, String skill_name) throws Exception {
        HttpManager manager = new HttpManager(context, endorseSkillUrl(user_id),
                JsonForMusicianServer.getJsonForEndorseSkill(skill_name),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void getMusician(Context context, HttpManager.HttpManagerCallback callback,
                                   int user_id) throws Exception {
        HttpManager manager = new HttpManager(context, getMusicianUrl(user_id),
                null, HttpManager.OPERATION_TYPE.OPERATION_TYPE_GET, callback);
        manager.execute();
    }

    public static void get_chats(Context context,
                                 HttpManager.HttpManagerCallback callback) throws Exception {
        HttpManager manager = new HttpManager(context, getChatsUrl(),
                UserPrefHelper.getLoggedUsername(),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void getMessages(Context context, HttpManager.HttpManagerCallback callback,
                                   int user_id) throws Exception {
        HttpManager manager = new HttpManager(context, getMessagesUrl(user_id),
                UserPrefHelper.getLoggedUsername(),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void sendMessage(Context context, HttpManager.HttpManagerCallback callback,
                                   int user_id, String message) throws Exception {
        HttpManager manager = new HttpManager(context, sendMessageUrl(user_id),
                JsonForMusicianServer.getJsonForSendMessage(message),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void bandList(Context context, HttpManager.HttpManagerCallback callback,
                                String query, List<String> skills) throws Exception {
        HttpManager manager = new HttpManager(context, bandListUrl(),
                JsonForMusicianServer.getJsonForBandList(query, skills),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void getBandInfo(Context context, HttpManager.HttpManagerCallback callback,
                                   int band_id) throws Exception {
        HttpManager manager = new HttpManager(context, getBandInfoUrl(band_id),
                UserPrefHelper.getLoggedUsername(),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void userCandidateBand(Context context, HttpManager.HttpManagerCallback callback,
                                   int band_id, List<String> check_skill) throws Exception {
        HttpManager manager = new HttpManager(context, userCandidateBandUrl(band_id),
                JsonForMusicianServer.getJsonForUserCandidate(check_skill),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void userRemoveCandBand(Context context, HttpManager.HttpManagerCallback callback,
                                         int band_id, int user_id) throws Exception {
        HttpManager manager = new HttpManager(context, userRemoveCandBandUrl(band_id),
                JsonForMusicianServer.getJsonForRemCand(user_id),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void userAcceptCandBand(Context context, HttpManager.HttpManagerCallback callback,
                                          int band_id, int user_id,
                                          List<String> check_skill_conf) throws Exception {
        HttpManager manager = new HttpManager(context, userAcceptCandBandUrl(band_id),
                JsonForMusicianServer.getJsonForAccCand(user_id, check_skill_conf),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void getBandMembers(Context context, HttpManager.HttpManagerCallback callback,
                                          int band_id, boolean candidates) throws Exception {
        HttpManager manager = new HttpManager(context, getBandMembersUrl(band_id),
                JsonForMusicianServer.getJsonForMembersBand(candidates),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void changeBio(Context context, HttpManager.HttpManagerCallback callback,
                                 String bio_text) throws Exception {
        HttpManager manager = new HttpManager(context, changeBioUrl(),
                JsonForMusicianServer.getJsontoChangeBio(bio_text),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void changeInfo(Context context, HttpManager.HttpManagerCallback callback,
                                  String gender_text, String city_text, String email_text, String phone_number_text) throws Exception {
        HttpManager manager = new HttpManager(context, changeInfoUrl(),
                JsonForMusicianServer.getJsontoChangeInfo(gender_text, city_text, email_text, phone_number_text),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void changeSkills(Context context, HttpManager.HttpManagerCallback callback,
                                    List<String> skills) throws Exception {
        HttpManager manager = new HttpManager(context, changeSkillsUrl(),
                JsonForMusicianServer.getJsontoChangeSkills(skills),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

    public static void view_ulikes(Context context, HttpManager.HttpManagerCallback callback,
                                   int post_id, int vote) throws Exception {
        HttpManager manager = new HttpManager(context, viewULikesUrl(),
                JsonForMusicianServer.getVoteJson(post_id,vote),
                HttpManager.OPERATION_TYPE.OPERATION_TYPE_POST, callback);
        manager.execute();
    }

}
