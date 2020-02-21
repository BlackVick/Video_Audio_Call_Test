package com.android.videoaudiocalltest.Models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    private String user_id;
    private String user_email;
    private String first_name;
    private String last_name;
    private String fcm_token;

    public User() {
    }

    public User(String user_id, String user_email, String first_name, String last_name, String fcm_token) {
        this.user_id = user_id;
        this.user_email = user_email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.fcm_token = fcm_token;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }
}
