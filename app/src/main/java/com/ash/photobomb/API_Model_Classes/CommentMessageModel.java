package com.ash.photobomb.API_Model_Classes;

public class CommentMessageModel {
    String user_id, user_name, profile_picture, group_id, media_id, parent_id, comments, status, creation, modified, id, reply_count;

    public CommentMessageModel(String user_id, String user_name, String profile_picture,
                               String group_id, String media_id, String parent_id,
                               String comments, String status, String creation,
                               String modified, String id, String reply_count) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.profile_picture = profile_picture;
        this.group_id = group_id;
        this.media_id = media_id;
        this.parent_id = parent_id;
        this.comments = comments;
        this.status = status;
        this.creation = creation;
        this.modified = modified;
        this.id = id;
        this.reply_count = reply_count;
    }

    public CommentMessageModel(String user_id, String user_name, String profile_picture,
                               String group_id, String media_id, String parent_id,
                               String comments, String status, String creation,
                               String modified, String id) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.profile_picture = profile_picture;
        this.group_id = group_id;
        this.media_id = media_id;
        this.parent_id = parent_id;
        this.comments = comments;
        this.status = status;
        this.creation = creation;
        this.modified = modified;
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreation() {
        return creation;
    }

    public void setCreation(String creation) {
        this.creation = creation;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReply_count() {
        return reply_count;
    }

    public void setReply_count(String reply_count) {
        this.reply_count = reply_count;
    }
}
