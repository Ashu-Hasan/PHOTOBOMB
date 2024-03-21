package com.ash.photobomb.API_Model_Classes;

public class GroupMemberModel {
    String _id, country_code, mobile, name, email, profile_picture, status, id, __v,
            group_id, user_id, is_admin, created, modified, admin_permission;

    public GroupMemberModel(String _id, String country_code, String mobile, String name, String email,
                            String profile_picture, String status, String id, String __v, String group_id, String user_id,
                            String is_admin, String created, String modified, String admin_permission) {
        this._id = _id;
        this.country_code = country_code;
        this.mobile = mobile;
        this.name = name;
        this.email = email;
        this.profile_picture = profile_picture;
        this.status = status;
        this.id = id;
        this.__v = __v;
        this.group_id = group_id;
        this.user_id = user_id;
        this.is_admin = is_admin;
        this.created = created;
        this.modified = modified;
        this.admin_permission = admin_permission;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String get__v() {
        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(String is_admin) {
        this.is_admin = is_admin;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getAdmin_permission() {
        return admin_permission;
    }

    public void setAdmin_permission(String admin_permission) {
        this.admin_permission = admin_permission;
    }
}
