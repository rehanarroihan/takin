package com.multazamgsd.takin.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Transaction {
    private String id;
    private String event_id;
    private String time;
    private String uid;

    public Transaction() {}

    public Transaction(String id, String event_id, String time, String uid) {
        this.id = id;
        this.event_id = event_id;
        this.time = time;
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
