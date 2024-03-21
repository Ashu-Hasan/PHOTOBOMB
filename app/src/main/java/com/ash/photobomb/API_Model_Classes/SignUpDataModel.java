package com.ash.photobomb.API_Model_Classes;

public class SignUpDataModel {
    String email, password, is_social, mobile, country_code, device_type, device_token, name, social_type, device_id;

    public SignUpDataModel(String email, String password, String is_social, String mobile,
                           String country_code, String device_type,
                           String device_token, String name,
                           String social_type, String device_id) {
        this.email = email;
        this.password = password;
        this.is_social = is_social;
        this.mobile = mobile;
        this.country_code = country_code;
        this.device_type = device_type;
        this.device_token = device_token;
        this.name = name;
        this.social_type = social_type;
        this.device_id = device_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIs_social() {
        return is_social;
    }

    public void setIs_social(String is_social) {
        this.is_social = is_social;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSocial_type() {
        return social_type;
    }

    public void setSocial_type(String social_type) {
        this.social_type = social_type;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
