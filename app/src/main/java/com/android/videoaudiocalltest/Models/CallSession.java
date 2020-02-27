package com.android.videoaudiocalltest.Models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class CallSession {

    private String session_id;
    private String caller_id;
    private String receiver_id;
    private String session_status;

    public CallSession() {
    }

    public CallSession(String session_id, String caller_id, String receiver_id, String session_status) {
        this.session_id = session_id;
        this.caller_id = caller_id;
        this.receiver_id = receiver_id;
        this.session_status = session_status;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getCaller_id() {
        return caller_id;
    }

    public void setCaller_id(String caller_id) {
        this.caller_id = caller_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getSession_status() {
        return session_status;
    }

    public void setSession_status(String session_status) {
        this.session_status = session_status;
    }
}
