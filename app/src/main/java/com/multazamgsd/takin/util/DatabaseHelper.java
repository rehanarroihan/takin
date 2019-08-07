package com.multazamgsd.takin.util;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.multazamgsd.takin.model.Event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper {
    private static String TAG = DatabaseHelper.class.getSimpleName();

    private static String TABLE_USER_NAME = "user";
    private static String TABLE_EVENT_NAME = "event";
    private static String TABLE_COMMENT_NAME = "comment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventRef = db.collection(TABLE_EVENT_NAME);
    private CollectionReference commentRef = db.collection(TABLE_COMMENT_NAME);

    // Util
    private String timeNow = new StringHelper().timeNow(); //yyyy/MM/dd HH:mm:ss

    public DatabaseHelper() {}

    public void updateUserData(String auth_type, String email, String uid, String full_name, String nick_name, String photo, String password) {
        Map<String, Object> user = new HashMap<>();
        user.put("auth_type", auth_type);
        user.put("email", email);
        user.put("full_name", full_name);
        user.put("nick_name", nick_name);
        user.put("photo", photo);
        user.put("password", password);
        user.put("last_login", timeNow);

        db.collection(TABLE_USER_NAME)
                .document(uid)
                .set(user, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "insertUserData operation successful");
                    } else {
                        Log.d(TAG, "insertUserData operation failed");
                    }
                });
    }

    public Query getEventList() {
        return eventRef.orderBy("publisher", Query.Direction.ASCENDING);
    }

    public void countEventComment(String event_id, CountEventCommentListener callback) {
        db.collection(TABLE_COMMENT_NAME)
                .whereEqualTo("event_id", event_id)
                .get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                callback.onComplete(String.valueOf(task.getResult().size()));
            }
        });
    }

    public void loadEventComment(String event_id, LoadEventCommentListener callback) {
        db.collection(TABLE_COMMENT_NAME)
                .whereEqualTo("event_id", event_id)
                .limit(5)
                .get().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    callback.onComplete(task);
                }
        });
    }

    public void sendEventComment(String event_id, String uid, String comment, SendEventCommentListener callback) {
        Map<String, Object> newComment = new HashMap<>();
        newComment.put("event_id", event_id);
        newComment.put("uid", uid);
        newComment.put("time", timeNow);
        newComment.put("comment", comment);
        newComment.put("like", "0");
        newComment.put("dislike", "0");

        db.collection(TABLE_COMMENT_NAME).add(newComment).addOnCompleteListener(task -> {
            if (task.isComplete()) {
                callback.onComplete(task);
            }
        });
    }

    public interface CountEventCommentListener {
        void onComplete(String commentCount);
    }

    public interface LoadEventCommentListener {
        void onComplete(Task task);
    }

    public interface SendEventCommentListener {
        void onComplete(Task task);
    }
}
