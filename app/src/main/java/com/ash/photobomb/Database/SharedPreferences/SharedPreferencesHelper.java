package com.ash.photobomb.Database.SharedPreferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;

import com.ash.photobomb.API_Classes.APIData;
import com.ash.photobomb.API_Classes.ApiController;
import com.ash.photobomb.API_Classes.ApiSet;
import com.ash.photobomb.API_Model_Classes.GroupInfoModel;
import com.ash.photobomb.API_Model_Classes.GroupMemberModel;
import com.ash.photobomb.API_Model_Classes.MediaDataModel;
import com.ash.photobomb.API_Model_Classes.SignUpDataModel;
import com.ash.photobomb.Constructor.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SharedPreferencesHelper {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    FirebaseStorage storage;
    FirebaseDatabase database;

    // creating a shared preference file
    @SuppressLint("NotConstructor")
    public void SharedPreferencesHelper(String fileName) {
        sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public SharedPreferencesHelper(Context context) {
        this.context = context;
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    // saving signUp details of user for data manipulation
    public void setSignUpUserData(SignUpDataModel signUpDataModel) {
        SharedPreferencesHelper("UserInputData");
        editor.putString("email", signUpDataModel.getEmail());
        editor.putString("password", signUpDataModel.getPassword());
        editor.putString("is_social", signUpDataModel.getIs_social());
        editor.putString("mobile", signUpDataModel.getMobile());
        editor.putString("country_code", signUpDataModel.getCountry_code());
        editor.putString("device_type", signUpDataModel.getDevice_id());
        editor.putString("device_token", signUpDataModel.getDevice_token());
        editor.putString("name", signUpDataModel.getName());
        editor.putString("social_type", signUpDataModel.getSocial_type());
        editor.putString("device_id", signUpDataModel.getDevice_id());
        editor.apply();
    }

    // fetching signUp details of user
    public SignUpDataModel getSignUpUserData() {

        SharedPreferencesHelper("UserInputData");

        return new SignUpDataModel(sharedPreferences.getString("email", "test@gmail.com"),
                sharedPreferences.getString("password", "0000"), sharedPreferences.getString("is_social", "0"),
                sharedPreferences.getString("mobile", "1234567890"), sharedPreferences.getString("country_code", "+91"),
                sharedPreferences.getString("device_type", "android"), sharedPreferences.getString("device_token", "kjbd8heuytuhu48"),
                sharedPreferences.getString("name", "ash"), sharedPreferences.getString("social_type", "android"),
                sharedPreferences.getString("device_id", "ene8en38hid"));


    }

    // deleting signUp details of user
    public void clearSignUpUserData() {
        SharedPreferencesHelper("UserInputData");
        editor.clear();
    }

    // saving the info of current user
    public void setCurrentUserData(User userData) {
        SharedPreferencesHelper("currentUser");
        editor.clear();
        editor.putString("email", userData.getEmail());
        editor.putString("password", userData.getPassword());
        editor.putString("is_social", userData.getIs_social());
        editor.putString("mobile", userData.getMobile());
        editor.putString("country_code", userData.getCountry_code());
        editor.putString("device_type", userData.getDevice_id());
        editor.putString("device_token", userData.getDevice_token());
        editor.putString("name", userData.getName());
        editor.putString("social_type", userData.getSocial_type());
        editor.putString("device_id", userData.getDevice_id());


        editor.putString("jwt", userData.getJwt());

        editor.putString("profile_picture", userData.getProfile_picture());
        editor.putString("user_type", userData.getUser_type());
        editor.putString("login_type", userData.getLogin_type());
        editor.putString("fb_id", userData.getFb_id());
        editor.putString("gmail_id", userData.getGmail_id());
        editor.putString("apple_id", userData.getApple_id());
        editor.putString("creation_time", userData.getCreation_time());
        editor.putString("is_guest", userData.getIs_guest());
        editor.putString("last_login", userData.getLast_login());
        editor.putString("otp_verification", userData.getOtp_verification());
        editor.putString("notification_status", userData.getNotification_status());
        editor.putString("status", userData.getStatus());
        editor.putString("social_token", userData.getSocial_token());
        editor.putString("is_member", userData.getIs_member());
        editor.putString("given_data", userData.getGiven_data());
        editor.putString("used_data", userData.getUsed_data());
        editor.putString("device_model", userData.getDevice_model());
        editor.putString("_id", userData.get_id());
        editor.putString("id", userData.getId());
        editor.putString("__v", userData.get__v());
        editor.apply();
    }


    // to get the info of current user
    public User getCurrentUserData() {
        SharedPreferencesHelper("currentUser");
        return new User(sharedPreferences.getString("jwt", "juynir98huneh398henuiehur3h9"), sharedPreferences.getString("country_code", "+91"),
                sharedPreferences.getString("mobile", "1234567890"), sharedPreferences.getString("name", "ash"),
                sharedPreferences.getString("email", "test@gmail.com"), sharedPreferences.getString("password", "0000"),
                sharedPreferences.getString("profile_picture", ""), sharedPreferences.getString("user_type", ""),
                sharedPreferences.getString("login_type", "1"), sharedPreferences.getString("fb_id", ""),
                sharedPreferences.getString("gmail_id", ""), sharedPreferences.getString("apple_id", ""),
                sharedPreferences.getString("is_social", "0"), sharedPreferences.getString("social_type", "android"),
                sharedPreferences.getString("device_type", "android"), sharedPreferences.getString("device_token", "kjbd8heuytuhu48"),
                sharedPreferences.getString("device_id", "ene8en38hid"), sharedPreferences.getString("creation_time", "1707457087428"),
                sharedPreferences.getString("is_guest", ""), sharedPreferences.getString("last_login", ""),
                sharedPreferences.getString("otp_verification", "0"), sharedPreferences.getString("notification_status", "0"),
                sharedPreferences.getString("status", "0"), sharedPreferences.getString("social_token", "0"),
                sharedPreferences.getString("is_member", "0"), sharedPreferences.getString("given_data", "0"),
                sharedPreferences.getString("used_data", "0"), sharedPreferences.getString("device_model", "0"),
                sharedPreferences.getString("_id", "65c5ba3f944124103e6f6dd6"), sharedPreferences.getString("id", "509"),
                sharedPreferences.getString("__v", "0")
        );
    }


    // to delete the info of current user
    public void clearCurrentUserData() {
        SharedPreferencesHelper("currentUser");
        editor.clear();
    }

    // to set and get the current version of our app
    public void setAppVersion(String appVersion) {
        SharedPreferencesHelper("appVersion");
        editor.putString("version", appVersion);
        editor.apply();
    }

    public String getAppVersion() {
        SharedPreferencesHelper("appVersion");
        return sharedPreferences.getString("version", "1");
    }

    // to set and get the current jwt of our app
    public void setJWTValue(String jwtValue) {
        SharedPreferencesHelper("jwt");
        editor.putString("jwtValue", jwtValue);
        editor.apply();
    }

    public String getJWTValue() {
        SharedPreferencesHelper("jwt");
        return sharedPreferences.getString("jwtValue", "");
    }


    // condition to switch layout or fragment on an activity by using backPress
    public void setConditionToSwitchLayout(int condition) {
        SharedPreferencesHelper("condition");
        editor.putInt("layoutCondition", condition);
        editor.apply();
    }

    public int getConditionToSwitchLayout() {
        SharedPreferencesHelper("condition");
        return sharedPreferences.getInt("layoutCondition", 1);
    }

    public void setCurrentGroup(GroupInfoModel groupData, boolean setId) {
        SharedPreferencesHelper("currentGroup");
        editor.clear();

        if (setId){
            editor.putString("group_id", groupData.getId());
        }
        else {
            editor.putString("group_id", groupData.getGroup_id());
        }

        editor.putString("_id", groupData.get_id());
        editor.putString("name", groupData.getName());
        editor.putString("image", groupData.getImage());
        editor.putString("expiring_date", groupData.getExpiring_date());
        editor.putString("grp_join_appr_wall", groupData.getGrp_join_appr_wall());
        editor.putString("is_start_now", groupData.getIs_start_now());
        editor.putString("strt_date", groupData.getStrt_date());
        editor.putString("pin_count", groupData.getPin_count());
        editor.putString("likes_count", groupData.getLikes_count());
        editor.putString("is_freeze", groupData.getIs_freeze());
        editor.putString("qr_code", groupData.getQr_code());
        editor.putString("key_qrcode", groupData.getKey_qrcode());
        editor.putString("is_disable_qr_code", groupData.getIs_disable_qr_code());
        editor.putString("status", groupData.getStatus());
        editor.putString("created_by", groupData.getCreated_by());
        editor.putString("creation_time", groupData.getCreation_time());
        editor.putString("update_time", groupData.getUpdate_time());
        editor.putString("id", groupData.getId());
        editor.putString("__v", groupData.get__v());

        editor.putString("is_admin", groupData.getIs_admin());
        editor.putString("created", groupData.getCreated());
        editor.putString("pined_id", groupData.getPined_id());
        editor.putString("pined_date", groupData.getPined_date());
        editor.putString("is_forty_hours", groupData.getIs_forty_hours());
        editor.putString("user_type", groupData.getUser_type());
        editor.apply();
    }

    public GroupInfoModel getCurrentGroup(){
        SharedPreferencesHelper("currentGroup");
        return new GroupInfoModel( sharedPreferences.getString("_id", ""),
                sharedPreferences.getString("name", ""),
                sharedPreferences.getString("image", ""),
                sharedPreferences.getString("expiring_date", ""),
                sharedPreferences.getString("grp_join_appr_wall", ""),
                sharedPreferences.getString("is_start_now", ""),
                sharedPreferences.getString("strt_date", ""),
                sharedPreferences.getString("pin_count", ""),
                sharedPreferences.getString("likes_count", ""),
                sharedPreferences.getString("is_freeze", ""),
                sharedPreferences.getString("qr_code", ""),
                sharedPreferences.getString("key_qrcode", ""),
                sharedPreferences.getString("is_disable_qr_code", ""),
                sharedPreferences.getString("status", ""),
                sharedPreferences.getString("created_by", ""),
                sharedPreferences.getString("creation_time", ""),
                sharedPreferences.getString("update_time", ""),
                sharedPreferences.getString("id", ""),
                sharedPreferences.getString("__v", ""),
                sharedPreferences.getString("group_id", ""),
                sharedPreferences.getString("is_admin", ""),
                sharedPreferences.getString("created", ""),
                sharedPreferences.getString("pined_id", ""),
                sharedPreferences.getString("pined_date", ""),
                sharedPreferences.getString("is_forty_hours", ""),
                sharedPreferences.getString("user_type", ""));
    }

    // to delete the info of current user
    public void clearCurrentGroup() {
        SharedPreferencesHelper("currentGroup");
        editor.clear();
    }




    // condition to To Check Like on group image
    public void setConditionToCheckLike(String imageId) {
        SharedPreferencesHelper(imageId);
        editor.putString("layoutCondition", "Y");
        editor.apply();
    }

    public String getConditionToCheckLike(String imageId) {
        SharedPreferencesHelper(imageId);
        return sharedPreferences.getString("layoutCondition", "");
    }

    public void clearConditionToCheckLike(String imageId) {
        SharedPreferencesHelper(imageId);
        editor.clear();
        editor.putString("layoutCondition", "N");
        editor.apply();
    }

    public void setCurrentMedia(MediaDataModel model) {
        SharedPreferencesHelper("currentMedia");
        editor.clear();
        editor.putString("media_url", model.getMedia_url());
        editor.putString("id", model.getId());
        editor.putString("media_type", model.getMedia_type());
        editor.putString("likes_count", model.getLikes_count());
        editor.putString("comment_count", model.getComment_count());
        editor.putString("added_by", model.getAdded_by());
        editor.putString("group_id", model.getGroup_id());
        editor.putString("added_by_name", model.getAdded_by_name());
        editor.putString("profile_picture", model.getProfile_picture());
        editor.putString("is_like", model.getIs_like());
        editor.putString("is_permission_deleted", model.getIs_permission_deleted());
        editor.putString("is_admin", model.getIs_admin());
        editor.apply();
    }

    public MediaDataModel getCurrentMedia() {
        SharedPreferencesHelper("currentMedia");
        return new MediaDataModel(sharedPreferences.getString("media_url", ""), sharedPreferences.getString("id", ""),
                sharedPreferences.getString("media_type", ""), sharedPreferences.getString("likes_count", ""),
                sharedPreferences.getString("comment_count", ""), sharedPreferences.getString("added_by", ""),
                sharedPreferences.getString("group_id", ""), sharedPreferences.getString("added_by_name", ""),
                sharedPreferences.getString("profile_picture", ""), sharedPreferences.getString("is_like", ""),
                sharedPreferences.getString("is_permission_deleted", ""), sharedPreferences.getString("is_admin", ""));
    }

    // condition to To Check Like on group image
    public void setIsSocial(String social) {
        // 0 for email and password login
        // 1 for google login
        // 2 for facebook login
        SharedPreferencesHelper("isSocial");
        editor.putString("social", social);
        editor.apply();
    }

    public String getIsSocial() {
        SharedPreferencesHelper("isSocial");
        return sharedPreferences.getString("social", "");
    }

    public void clearIsSocial(String social) {
        SharedPreferencesHelper("isSocial");
        editor.clear();
        editor.apply();
    }

}
