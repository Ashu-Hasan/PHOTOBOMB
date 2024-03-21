package com.ash.photobomb.API_Classes;

import static com.ash.photobomb.API_Classes.ApiController.context;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.ash.photobomb.API_Model_Classes.LoginAuthenticationModel;
import com.ash.photobomb.API_Model_Classes.ResponseModel;
import com.ash.photobomb.API_Model_Classes.SignUpDataModel;
import com.ash.photobomb.MainActivity;
import com.ash.photobomb.ScanQRCode;
import com.ash.photobomb.other.StorageFiles.Method;
import com.ash.photobomb.other.StorageFiles.StorageUtil;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiSet extends APIData {

    // to check interNet connected or not
    static boolean Connected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    static void showAlertDialog(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Alert")
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
        builder.show();

    }

    public static Uri downloadImage(Context context,Uri uri){
        final Uri[] result = {null};

        Picasso.get().load(uri).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                File file = new File(context.getExternalCacheDir(), "share_image.png");
                try {
                    FileOutputStream outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    // Use FileProvider to get a content:// URI
                    Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);

                    result[0] = contentUri;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

        return result[0];
    }

    static String getCurrentDate(){
        // Get the current date and time
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Format the date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(currentDate);
    }

    static JSONObject getResponseData(Response<JsonObject> response) {
        JSONObject jsonResponse = null;
        if (response.body() != null) {
            JsonObject jsonObject = response.body();

            try {
                jsonResponse = new JSONObject(jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonResponse;
    }

    static String convertTimestampIntoDate(long value, String responseTitle) {
        String result = "";
        String time = "", numberDate = "";
        long timestamp = value * 1000; // Assuming the timestamp is in seconds, multiply by 1000 to convert to milliseconds

        Date date = new Date(timestamp);

        // to get time
        if (date.getHours() > 12){
            time = String.valueOf(date.getHours() % 12)+":" + String.valueOf(date.getMinutes()) + " pm";
        }
        else {time = String.valueOf(date.getHours())+":" + String.valueOf(date.getMinutes()) + " am";}

        // get date
        String yearTemp = String.valueOf(date.getYear());
        String year = String.valueOf(yearTemp.charAt(yearTemp.length()-1));
        numberDate = String.valueOf(date.getDate()) + ":"+ String.valueOf(date.getMonth())+":"+ year;
        if (String.valueOf(date.getYear()).length() > 1){
            numberDate = String.valueOf(date.getDate()) + ":"+ String.valueOf(date.getMonth())+":"
                    +String.valueOf(yearTemp.charAt(yearTemp.length()-2))+ year;
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault()); // Set the desired time zone

        if (responseTitle.equals("time")){
            result = time;
        }
        else if (responseTitle.equals("date")){
            result = numberDate;
        }
        else {
            result = numberDate + " | "+ time;
        }

//        return sdf.format(date);
        return result;
    }


    // get app version data
    @GET(appVersionAPIFile)
    Call<JsonObject> getVersionInfo();

    // set to sign up user
    @FormUrlEncoded
    @POST(signUpAPIFile)
    Call<JsonObject> getregister(
            @Field("email") String email,
            @Field("password") String password,
            @Field("is_social") String is_social,
            @Field("mobile") String mobile,
            @Field("country_code") String country_code,
            @Field("device_type") String device_type,
            @Field("device_token") String device_token,
            @Field("name") String name,
            @Field("social_type") String social_type,
            @Field("device_id") String device_id
    );

    // set to send otp to user
    @FormUrlEncoded
    @POST(otpSendForRegistrationAPIFile)
    Call<JsonObject> otpSendForRegistration(
            @Field("email") String email,
            @Field("mobile") String mobile,
            @Field("country_code") String country_code
    );

    // set to verify otp to user
    @FormUrlEncoded
    @POST(otpVerifyForRegistrationAPIFile)
    Call<JsonObject> otpVerificationForRegistration(
            @Field("otp") String otp,
            @Field("mobile") String mobile,
            @Field("country_code") String country_code
    );

    // set to login user
    @FormUrlEncoded
    @POST(loginAPIFile)
    Call<JsonObject> getAuthenticationData(
            @Field("username") String email,
            @Field("password") String password,
            @Field("is_social") String is_social,
            @Field("device_type") String device_type,
            @Field("device_token") String device_token
    );

    // set to login user with fb
    @FormUrlEncoded
    @POST(loginAPIFile)
    Call<JsonObject> getAuthenticationDataOfFB(
            @Field("name") String name,
            @Field("is_social") String is_social,
            @Field("device_type") String device_type,
            @Field("device_token") String device_token,
            @Field("social_type") String social_type,
            @Field("social_token") String social_token
    );

    // set to login user with google
    @FormUrlEncoded
    @POST(loginAPIFile)
    Call<JsonObject> getAuthenticationDataOfGoogle(
            @Field("email") String email,
            @Field("name") String name,
            @Field("is_social") String is_social,
            @Field("device_type") String device_type,
            @Field("device_token") String device_token,
            @Field("social_type") String social_type,
            @Field("social_token") String social_token
    );

    // set to get user profile data
    @POST(getUserProfileAPIFile)
    Call<JsonObject> getUserProfileData();


    // set to update user profile data
    @FormUrlEncoded
    @POST(updateUserProfileAPIFile)
    Call<JsonObject> updateUserProfileData(
            @Field("name") String name,
            @Field("email") String email,
            @Field("country_code") String country_code,
            @Field("mobile") String mobile,
            @Field("profile_picture") String profile_picture,
            @Field("jwt") String jwt
    );


    // set to send feedback of user
    @FormUrlEncoded
    @POST(sendFeedbackAPIFile)
    Call<JsonObject> sendFeedback(
            @Field("full_name") String full_name,
            @Field("email") String email,
            @Field("c_code") String country_code,
            @Field("mobile") String mobile,
            @Field("message") String message
    );

    // set to send otp to reset password of user
    @FormUrlEncoded
    @POST(sendOTPForResetPasswordAPIFile)
    Call<JsonObject> sendOtpToResetPassword(
            @Field("country_code") String country_code,
            @Field("mobile") String mobile
    );


    // set to verify otp to reset password of user
    @FormUrlEncoded
    @POST(verifyOTPForResetPasswordAPIFile)
    Call<JsonObject> verifyOtpToResetPassword(
            @Field("otp") String otp,
            @Field("mobile") String mobile,
            @Field("country_code") String country_code

    );

    // reset password of user
    @FormUrlEncoded
    @POST(resetPasswordAPIFile)
    Call<JsonObject> resetPassword(
            @Field("password") String password,
            @Field("user_id") String user_id
    );

    // get Terms and condition data
    @POST(termsConditionAPIFile)
    Call<JsonObject> getTerm();


    // to create group
    @FormUrlEncoded
    @POST(createGroupAPIFile)
    Call<JsonObject> createGroup(
            @Field("name") String name,
            @Field("image") String image,
            @Field("expiring_date") String expiring_date,
            @Field("grp_join_appr_wall") String grp_join_appr_wall,
            @Field("is_start_now") String is_start_now,
            @Field("strt_date") String strt_date,
            @Field("group_users_mobile") String group_users_mobile

    );

    // to get all groups
    @GET(getGroupsAPIFile)
    Call<JsonObject> getGroups();

    // to create group
    @FormUrlEncoded
    @POST(searchGroupsAPIFile)
    Call<JsonObject> searchGroups(
            @Field("keyword") String keyword
    );

    // to create group
    @FormUrlEncoded
    @POST(editGroupAPIFile)
    Call<JsonObject> editGroup(
            @Field("group_id") String group_id,
            @Field("name") String name,
            @Field("image") String image,
            @Field("expiring_date") String expiring_date,
            @Field("grp_join_appr_wall") String grp_join_appr_wall,
            @Field("is_start_now") String is_start_now,
            @Field("strt_date") String strt_date

    );

    // to create group
    @FormUrlEncoded
    @POST(groupMemberListAPIFile)
    Call<JsonObject> getGroupMembers(
            @Field("group_id") String group_id
    );

    // add media files in group
    @FormUrlEncoded
    @POST(addGroupMediaFileAPIFile)
    Call<JsonObject> addGroupMediaFile(
            @Field("group_id") String group_id,
            @Field("media_url") String media_url
    );

    // get media files in group
    @FormUrlEncoded
    @POST(getGroupMediaListNewAPIFile)
    Call<JsonObject> getGroupMediaFile(
            @Field("group_id") String group_id
    );

    @FormUrlEncoded
    @POST(getGroupMediaListAllAPIFile)
    Call<JsonObject> getGroupMediaListAll(
            @Field("group_id") String group_id
    );

    // to create group
    @FormUrlEncoded
    @POST(getGroupDetailAPIFile)
    Call<JsonObject> getGroupDetail(
            @Field("group_id") String group_id
    );

    // to disable_enable_qr_code of group
    @FormUrlEncoded
    @POST(disableEnableQRCodeAPIFile)
    Call<JsonObject> disableEnableQRCode(
            @Field("group_id") String group_id,
            @Field("is_disable_qr_code") String is_disable_qr_code
    );

    // to refresh QR Code of group
    @FormUrlEncoded
    @POST(refreshQRCodeAPIFile)
    Call<JsonObject> refreshQRCode(
            @Field("group_id") String group_id
    );

    // to delete group media File
    @FormUrlEncoded
    @POST(deleteGroupMediaAPIFile)
    Call<JsonObject> deleteGroupMediaFile(
            @Field("group_id") String group_id,
            @Field("media_id") String media_id
    );

    // change group settings
    @FormUrlEncoded
    @POST(changeGroupSettingAPIFile)
    Call<JsonObject> changeGroupSettings(
            @Field("group_id") String group_id,
            @Field("expiring_date") String expiring_date,
            @Field("grp_join_appr_wall") String grp_join_appr_wall
    );

    // add group members
    @FormUrlEncoded
    @POST(addGroupMemberAPIFile)
    Call<JsonObject> addGroupMember(
            @Field("group_id") String group_id,
            @Field("group_users") String group_users
    );

    // add group admin
    @FormUrlEncoded
    @POST(makeGroupAdminAPIFile)
    Call<JsonObject> makeGroupAdmin(
            @Field("group_id") String group_id,
            @Field("req_id") String req_id,
            @Field("user_id") String user_id
    );

    // send group join request
    @FormUrlEncoded
    @POST(sendGroupJoinReqAPIFile)
    Call<JsonObject> sendGroupJoinRequest(
            @Field("act_by") String act_by,
            @Field("act_type") String act_type,
            @Field("group_id") String group_id,
            @Field("key_qrcode") String key_qrcode,
            @Field("by_request") String by_request
    );

    // get requests
    @POST(getRequestListAPIFile)
    Call<JsonObject> getRequestList();

    // get requests
    @FormUrlEncoded
    @POST(performRequestActionAPIFile)
    Call<JsonObject> performRequestAction(
            @Field("user_id") String user_id,
            @Field("group_id") String group_id,
            @Field("req_id") String req_id,
            @Field("action_taken") String action_taken
    );

    // get Notifications
    @GET(getNotificationListAPIFile)
    Call<JsonObject> getNotificationList();



    // to like media files in group
    @FormUrlEncoded
    @POST(deleteNotificationAPIFile)
    Call<JsonObject> deleteNotification(
            @Field("notification_id") String notification_id
    );

    // to like media files in group
    @GET(deleteAllNotificationAPIFile)
    Call<JsonObject> deleteAllNotification();

    // to like media files in group
    @FormUrlEncoded
    @POST(likeMediaGroupAPIFile)
    Call<JsonObject> likeMediaGroup(
            @Field("group_id") String group_id,
            @Field("media_id") String media_id
    );

    // to unlike media files in group
    @FormUrlEncoded
    @POST(unlikeMediaGroupAPIFile)
    Call<JsonObject> unlikeMediaGroup(
            @Field("group_id") String group_id,
            @Field("media_id") String media_id
    );

    // to get media comments files in group
    @FormUrlEncoded
    @POST(mediaCommentListAPIFile)
    Call<JsonObject> getMediaCommentList(
            @Field("group_id") String group_id,
            @Field("media_id") String media_id
    );

    // to add media comment files in group
    @FormUrlEncoded
    @POST(addMediaCommentAPIFile)
    Call<JsonObject> addMediaComment(
            @Field("group_id") String group_id,
            @Field("media_id") String media_id,
            @Field("parent_id") String parent_id,
            @Field("comments") String comments
    );

    // to edit media media files in group
    @FormUrlEncoded
    @POST(editMediaCommentAPIFile)
    Call<JsonObject> editMediaComment(
            @Field("group_id") String group_id,
            @Field("media_id") String media_id,
            @Field("comment_id") String comment_id,
            @Field("comments") String comments
    );

    // to delete media media files in group
    @FormUrlEncoded
    @POST(deleteMediaGroupCommentAPIFile)
    Call<JsonObject> deleteMediaGroupComment(
            @Field("group_id") String group_id,
            @Field("media_id") String media_id,
            @Field("comment_id") String comment_id
    );

    // to get reply of media files in group
    @FormUrlEncoded
    @POST(mediaCommentReplyListAPIFile)
    Call<JsonObject> getMediaCommentReply(
            @Field("group_id") String group_id,
            @Field("media_id") String media_id,
            @Field("parent_id") String parent_id
    );

    // to freeze Group
    @FormUrlEncoded
    @POST(freezeGroupAPIFile)
    Call<JsonObject> freezeGroup(
            @Field("group_id") String group_id,
            @Field("is_freeze") String is_freeze
    );

    // to delete Group
    @FormUrlEncoded
    @POST(groupDeleteAPIFile)
    Call<JsonObject> groupDelete(
            @Field("group_id") String group_id
    );


    // to leve Group
    @FormUrlEncoded
    @POST(leaveGroupAPIFile)
    Call<JsonObject> leaveGroup(
            @Field("group_id") String group_id
    );

    // to to get Subscription details
    @GET(getProfileSubscriptionAPIFile)
    Call<JsonObject> getProfileSubscription();

    // to to get plan details
    @GET(getUserPlanAPIFile)
    Call<JsonObject> getUserPlan();

    // to to get plan details
    @FormUrlEncoded
    @POST(purchaseSubcriptionPlanAPIFile)
    Call<JsonObject> purchaseSubcriptionPlan(
            @Field("plan_id") String plan_id,
            @Field("plan_validity") String plan_validity,
            @Field("plan_data") String plan_data,
            @Field("payment_type") String payment_type,
            @Field("payment_price") String payment_price,
            @Field("payment_date") String payment_date,
            @Field("transactions_id") String transactions_id
    );
}
