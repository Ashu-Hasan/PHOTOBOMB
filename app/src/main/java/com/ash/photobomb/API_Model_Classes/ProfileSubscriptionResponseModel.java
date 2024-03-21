package com.ash.photobomb.API_Model_Classes;

public class ProfileSubscriptionResponseModel {
    String user_type, login_type, social_type, creation_time, _id, id, size, used_data, givin_space, remaining_days, used_data_gb;

    public ProfileSubscriptionResponseModel(String user_type, String login_type, String social_type,
                                            String creation_time, String _id, String id, String size,
                                            String used_data, String givin_space, String remaining_days, String used_data_gb) {
        this.user_type = user_type;
        this.login_type = login_type;
        this.social_type = social_type;
        this.creation_time = creation_time;
        this._id = _id;
        this.id = id;
        this.size = size;
        this.used_data = used_data;
        this.givin_space = givin_space;
        this.remaining_days = remaining_days;
        this.used_data_gb = used_data_gb;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getLogin_type() {
        return login_type;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }

    public String getSocial_type() {
        return social_type;
    }

    public void setSocial_type(String social_type) {
        this.social_type = social_type;
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUsed_data() {
        return used_data;
    }

    public void setUsed_data(String used_data) {
        this.used_data = used_data;
    }

    public String getGivin_space() {
        return givin_space;
    }

    public void setGivin_space(String givin_space) {
        this.givin_space = givin_space;
    }

    public String getRemaining_days() {
        return remaining_days;
    }

    public void setRemaining_days(String remaining_days) {
        this.remaining_days = remaining_days;
    }

    public String getUsed_data_gb() {
        return used_data_gb;
    }

    public void setUsed_data_gb(String used_data_gb) {
        this.used_data_gb = used_data_gb;
    }
}
