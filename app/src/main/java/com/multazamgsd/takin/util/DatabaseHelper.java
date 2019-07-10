package com.multazamgsd.takin.util;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    public DatabaseHelper() {}

    public void updateUserData(String auth_type, String email, String uid, String full_name, String nick_name, String photo, String password) {
        Map<String, Object> user = new HashMap<>();
        user.put("auth_type", auth_type);
        user.put("email", email);
        user.put("full_name", full_name);
        user.put("nick_name", nick_name);
        user.put("photo", photo);
        user.put("password", password);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));

        user.put("last_login", dateFormat.format(date));

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
}
