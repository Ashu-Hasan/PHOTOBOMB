package com.ash.photobomb.API_Model_Classes;

public class PlanModel {
    String plan_name, validity, paid_price, paid_data, status, creation_time, modified_time, _id, id, __v, plan_id;

    public PlanModel(String plan_name, String validity, String paid_price,
                     String paid_data, String status, String creation_time,
                     String modified_time, String _id, String id, String __v, String plan_id) {
        this.plan_name = plan_name;
        this.validity = validity;
        this.paid_price = paid_price;
        this.paid_data = paid_data;
        this.status = status;
        this.creation_time = creation_time;
        this.modified_time = modified_time;
        this._id = _id;
        this.id = id;
        this.__v = __v;
        this.plan_id = plan_id;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getPaid_price() {
        return paid_price;
    }

    public void setPaid_price(String paid_price) {
        this.paid_price = paid_price;
    }

    public String getPaid_data() {
        return paid_data;
    }

    public void setPaid_data(String paid_data) {
        this.paid_data = paid_data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    public String getModified_time() {
        return modified_time;
    }

    public void setModified_time(String modified_time) {
        this.modified_time = modified_time;
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

    public String get__v() {
        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }
}
