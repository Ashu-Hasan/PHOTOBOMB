package com.ash.photobomb.API_Model_Classes;

public class LoginAuthenticationModel {
    String email, password, is_social, mobile_no, login_type, country_code, device_type, device_token;

    public LoginAuthenticationModel(String email, String password, String is_social,
                                    String mobile_no, String login_type, String country_code,
                                    String device_type, String device_token) {
        this.email = email;
        this.password = password;
        this.is_social = is_social;
        this.mobile_no = mobile_no;
        this.login_type = login_type;
        this.country_code = country_code;
        this.device_type = device_type;
        this.device_token = device_token;
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

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getLogin_type() {
        return login_type;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
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
}
