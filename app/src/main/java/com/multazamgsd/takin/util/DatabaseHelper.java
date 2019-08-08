package com.multazamgsd.takin.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.multazamgsd.takin.model.Comment;
import com.multazamgsd.takin.model.Event;
import com.multazamgsd.takin.model.User;

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

    public void updateUserData(User user, UpdateUserDataListener callback) {
        db.collection(TABLE_USER_NAME)
                .document(user.getUid())
                .set(user, SetOptions.merge())
                .addOnCompleteListener(task -> callback.onComplete(task));
    }

    public Query getEventList() {
        return eventRef.orderBy("publisher", Query.Direction.ASCENDING);
    }

    public void getUserDetailFromUID(String uid, GetUserDetailFromUIDListener callback) {
        DocumentReference docRef = db.collection(TABLE_USER_NAME).document(uid);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                callback.onComplete(documentSnapshot.toObject(User.class));
            } else {
                Log.d(TAG, String.format("UID: %s not exist", uid));
                callback.onComplete(documentSnapshot.toObject(User.class));
            }
        });
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
        ArrayList<Comment> commentResult = new ArrayList<>();
        db.collection(TABLE_COMMENT_NAME)
                .whereEqualTo("event_id", event_id)
                .limit(5)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            Comment commentItem = new Comment();
                            commentItem.setId(doc.getId());
                            commentItem.setComment(doc.get("comment").toString());
                            commentItem.setDislike(doc.get("dislike").toString());
                            commentItem.setLike(doc.get("like").toString());
                            commentItem.setTime(doc.get("time").toString());
                            commentItem.setUid(doc.get("uid").toString());
                            // Joining user doc to comment
                            getUserDetailFromUID(commentItem.getUid(), userResult -> {
                                commentItem.setPict(userResult.getPhoto());
                                commentItem.setNick_name(userResult.getFirst_name());
                            });
                            commentResult.add(commentItem);
                        }
                        callback.onComplete(commentResult);
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

    public interface UpdateUserDataListener {
        void onComplete(Task task);
    }

    public interface GetUserDetailFromUIDListener {
        void onComplete(User user);
    }

    public interface CountEventCommentListener {
        void onComplete(String commentCount);
    }

    public interface LoadEventCommentListener {
        void onComplete(ArrayList<Comment> result);
    }

    public interface SendEventCommentListener {
        void onComplete(Task task);
    }
}
