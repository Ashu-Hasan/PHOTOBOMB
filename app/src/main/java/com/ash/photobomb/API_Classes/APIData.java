package com.ash.photobomb.API_Classes;

public interface APIData {

    // Api fileName

    // file name of app version api
    String appVersionAPIFile = "version/get_version";

    // file name of login api
    String loginAPIFile = "login/login_authentication";

    // file name of signUp api
    String signUpAPIFile = "register/signup";

    // file name of otp sender api
    String otpSendForRegistrationAPIFile = "otp/send_otp_registration";

    // file name of otp Verifier api
    String otpVerifyForRegistrationAPIFile = "otp/verify_otp_registration";

    // file name of get user profile api
    String getUserProfileAPIFile = "users/get_user_profile";

    // file name of update user profile api
    String updateUserProfileAPIFile = "users/update_profile";

    // file name of feedback sender api used for contact us page
    String sendFeedbackAPIFile = "contact/send_request";

    // file name of otp sender api used for resetPassword
    String sendOTPForResetPasswordAPIFile = "otp/check_user_and_send_otp";

    // file name of otp sender api used for resetPassword
    String verifyOTPForResetPasswordAPIFile = "otp/verify_otp_forgot";

    // file name of otp sender api used for resetPassword
    String resetPasswordAPIFile = "users/reset_password";

    // file name of terms and condition api used for resetPassword
    String termsConditionAPIFile = "pages/get_dynamic_pages?page_id=2";

    // file name of Create group api used for Create group
    String createGroupAPIFile = "groups/create_group";

    // file name of gat groups api used for Create group
    String getGroupsAPIFile = "groups/get_groups";

    // file name of gat groups api used for Create group
    String searchGroupsAPIFile = "groups/searching_group";

    // file name of gat groups api used for Create group
    String editGroupAPIFile = "groups/edit_group";

    // file name of disable Enable QR api used for disable group (use group link)
    String disableEnableQRCodeAPIFile = "groups/disable_enable_qr_code";

    // file name of refresh qrcode api used for refresh group (use group link)
    String refreshQRCodeAPIFile = "groups/refresh_qrcode";

    // file name of group Member List api used for get group Member info (use group link)
    String groupMemberListAPIFile = "groups/group_member_list";

//    file name of group Member List api used for get group Member info (use group link)  it is an updated file of group_media_list_all file
    String getGroupMediaListNewAPIFile = "groups/group_media_list_new";
    String getGroupMediaListAllAPIFile = "groups/group_media_list_all";

    // file name of group Member List api used for get group Member info (use group link)
    String addGroupMediaFileAPIFile = "groups/add_group_media_file";

    // file name of get Group Detail api used for get group info (use group link)
    String getGroupDetailAPIFile = "groups/get_group_detail";

    // file name of get Group Detail api used for get group info (use group link)
    String deleteGroupMediaAPIFile = "groups/delete_group_media";

    // file name of change Group Setting api used for change Group Setting  (use group link)
    String changeGroupSettingAPIFile = "groups/change_setting";

    // file name of add Group Member APIFile used to add members in Group (use group link)
    String addGroupMemberAPIFile = "groups/add_group_member";

    // file name of add Group Member APIFile used to add members in Group (use group link)
    String makeGroupAdminAPIFile = "groups/add_group_admin";

    // file name of send Group JoinReq APIFile used to add members in Group
    String sendGroupJoinReqAPIFile = "request/send_group_join_req";

    // file name of get Request List APIFile used to get Group request
    String getRequestListAPIFile = "request/get_reqeust_list";

    // file name of perform Request Action APIFile used to perform Group request
    String performRequestActionAPIFile = "request/perform_request_action";

    // file name of send Group JoinReq APIFile used to add members in Group
    String getNotificationListAPIFile = "notification/get_notification_list";

    // file name of send Group JoinReq APIFile used to add members in Group
    String readNotificationAPIFile = "notification/read_notification";

    // file name of send Group JoinReq APIFile used to add members in Group
    String deleteNotificationAPIFile = "notification/delete_notification";

    // file name of send Group JoinReq APIFile used to add members in Group
    String deleteAllNotificationAPIFile = "notification/delete_all_notifications";

    // file name of like Media Group APIFile used to like media in Group
    String likeMediaGroupAPIFile = "like/like_media_group";

    // file name of unlike Media Group APIFile used to unlike media in Group
    String unlikeMediaGroupAPIFile = "like/unlike_media_group";

    // file name of media Comment List APIFile used to get comment of media in Group
    String mediaCommentListAPIFile = "comment/media_comment_list";

    // file name of add Media Comment APIFile used to add comment on media in Group
    String addMediaCommentAPIFile = "comment/add_media_comment";

    // file name of edit Media Comment APIFile used to edit comment on media in Group
    String editMediaCommentAPIFile = "comment/edit_media_comment";

    // file name of edit Media Comment APIFile used to edit comment on media in Group
    String deleteMediaGroupCommentAPIFile = "comment/delete_media_group_comment";

    // file name of edit Media Comment APIFile used to edit comment on media in Group
    String mediaCommentReplyListAPIFile = "comment/media_comment_reply_list";

    // file name of freeze Group APIFile used to edit comment on media in Group
    String freezeGroupAPIFile = "groups/freeze_group";

    // file name of freeze Group APIFile used to edit comment on media in Group
    String groupDeleteAPIFile = "groups/group_delete";

    // file name of freeze Group APIFile used to edit comment on media in Group
    String leaveGroupAPIFile = "groups/leave_group";

    // file name of getProfileSubscription APIFile  used to get Subscription details
    String getProfileSubscriptionAPIFile = "subscription/get_profile_subscription";

    // file name of getProfileSubscription APIFile  used to get Subscription details
    String getUserPlanAPIFile = "plans/get_user_plan";

    // file name of getProfileSubscription APIFile  used to get Subscription details
    String purchaseSubcriptionPlanAPIFile = "subscription/purchase_subcription_plan";








    // ApiLinks....................
    String api = "https://testing.myphotobomb.com/";


}
