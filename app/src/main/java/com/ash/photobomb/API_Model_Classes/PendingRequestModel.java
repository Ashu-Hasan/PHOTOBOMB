package com.ash.photobomb.API_Model_Classes;

public class PendingRequestModel {
    String _id, name, status, id, __v, group_id, user_id, act_type, act_by, by_request, is_qrcode, created, modified;

    public PendingRequestModel(String _id, String name, String status, String id,
                               String __v, String group_id, String user_id, String act_type,
                               String act_by, String by_request, String is_qrcode, String created, String modified) {
        this._id = _id;
        this.name = name;
        this.status = status;
        this.id = id;
        this.__v = __v;
        this.group_id = group_id;
        this.user_id = user_id;
        this.act_type = act_type;
        this.act_by = act_by;
        this.by_request = by_request;
        this.is_qrcode = is_qrcode;
        this.created = created;
        this.modified = modified;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAct_type() {
        return act_type;
    }

    public void setAct_type(String act_type) {
        this.act_type = act_type;
    }

    public String getAct_by() {
        return act_by;
    }

    public void setAct_by(String act_by) {
        this.act_by = act_by;
    }

    public String getBy_request() {
        return by_request;
    }

    public void setBy_request(String by_request) {
        this.by_request = by_request;
    }

    public String getIs_qrcode() {
        return is_qrcode;
    }

    public void setIs_qrcode(String is_qrcode) {
        this.is_qrcode = is_qrcode;
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
}
