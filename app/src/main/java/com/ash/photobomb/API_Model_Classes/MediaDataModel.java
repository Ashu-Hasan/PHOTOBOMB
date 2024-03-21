package com.ash.photobomb.API_Model_Classes;

public class MediaDataModel {
    String media_url, id, media_type, likes_count,
            comment_count, added_by, group_id, added_by_name,
            profile_picture, is_like, is_permission_deleted, is_admin;
    int imageSetPosition;

    public MediaDataModel(String media_url, String id) {
        this.media_url = media_url;
        this.id = id;
    }

    public MediaDataModel(String media_url, String id, String media_type,
                          String likes_count, String comment_count, String added_by,
                          String group_id, String added_by_name, String profile_picture,
                          String is_like, String is_permission_deleted, String is_admin) {
        this.media_url = media_url;
        this.id = id;
        this.media_type = media_type;
        this.likes_count = likes_count;
        this.comment_count = comment_count;
        this.added_by = added_by;
        this.group_id = group_id;
        this.added_by_name = added_by_name;
        this.profile_picture = profile_picture;
        this.is_like = is_like;
        this.is_permission_deleted = is_permission_deleted;
        this.is_admin = is_admin;
    }

    public MediaDataModel(String media_url, String id, String media_type, String likes_count,
                          String comment_count, String added_by, String group_id,
                          String added_by_name, String profile_picture,
                          String is_like, String is_permission_deleted,
                          String is_admin, int imageSetPosition) {
        this.media_url = media_url;
        this.id = id;
        this.media_type = media_type;
        this.likes_count = likes_count;
        this.comment_count = comment_count;
        this.added_by = added_by;
        this.group_id = group_id;
        this.added_by_name = added_by_name;
        this.profile_picture = profile_picture;
        this.is_like = is_like;
        this.is_permission_deleted = is_permission_deleted;
        this.is_admin = is_admin;
        this.imageSetPosition = imageSetPosition;
    }

    public int getImageSetPosition() {
        return imageSetPosition;
    }

    public void setImageSetPosition(int imageSetPosition) {
        this.imageSetPosition = imageSetPosition;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(String likes_count) {
        this.likes_count = likes_count;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public String getAdded_by() {
        return added_by;
    }

    public void setAdded_by(String added_by) {
        this.added_by = added_by;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getAdded_by_name() {
        return added_by_name;
    }

    public void setAdded_by_name(String added_by_name) {
        this.added_by_name = added_by_name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getIs_like() {
        return is_like;
    }

    public void setIs_like(String is_like) {
        this.is_like = is_like;
    }

    public String getIs_permission_deleted() {
        return is_permission_deleted;
    }

    public void setIs_permission_deleted(String is_permission_deleted) {
        this.is_permission_deleted = is_permission_deleted;
    }

    public String getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(String is_admin) {
        this.is_admin = is_admin;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
