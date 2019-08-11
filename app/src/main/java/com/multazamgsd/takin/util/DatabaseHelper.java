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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.GsonBuilder;
import com.multazamgsd.takin.model.Comment;
import com.multazamgsd.takin.model.Event;
import com.multazamgsd.takin.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {
    private static String TAG = DatabaseHelper.class.getSimpleName();

    private static String TABLE_USER_NAME = "user";
    private static String TABLE_EVENT_NAME = "event";
    private static String TABLE_COMMENT_NAME = "comment";
    private static String TABLE_TRANSACTION_NAME = "transaction";
    private static String TABLE_LIKE_NAME = "like";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference eventRef = db.collection(TABLE_EVENT_NAME);
    private CollectionReference userRef = db.collection(TABLE_USER_NAME);
    private CollectionReference commentRef = db.collection(TABLE_COMMENT_NAME);
    private CollectionReference transactionRef = db.collection(TABLE_TRANSACTION_NAME);
    private CollectionReference likeRef = db.collection(TABLE_LIKE_NAME);

    // Util
    private String timeNow = new StringHelper().timeNow(); //yyyy/MM/dd HH:mm:ss

    public DatabaseHelper() {}

    public void updateUserDataOnLogin(String uid, TaskCompleteListener callback) {
        userRef
            .document(uid)
            .update("last_login", new StringHelper().timeNow())
            .addOnCompleteListener(task -> callback.onComplete(task));
    }

    public void updateUserData(User user, TaskCompleteListener callback) {
        userRef
            .document(user.getUid())
            .set(user, SetOptions.merge())
            .addOnCompleteListener(task -> callback.onComplete(task));
    }

    public void checkFieldExist(String table, String id, CheckFieldExistListener callback) {
        DocumentReference docRef = db.collection(table).document(id);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                callback.onComplete(true);
            } else {
                callback.onComplete(false);
            }
        }).addOnFailureListener(e -> {
            callback.onComplete(false);
        });
    }

    public void checkEventLiked(String eventId, String uid, CheckLikedExistListener callback) {
        likeRef
            .whereEqualTo("uid", uid)
            .whereEqualTo("event_id", eventId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {

            if (queryDocumentSnapshots.size() > 0) {
                callback.onComplete(queryDocumentSnapshots.getDocuments().get(0).getId());
            } else {
                callback.onComplete(null);
            }
        }).addOnFailureListener(e -> {
            callback.onComplete(null);
        });
    }

    public Query getEventList() {
        return eventRef.orderBy("publisher", Query.Direction.ASCENDING);
    }

    public void getUserDetailFromUID(String uid, GetUserDetailFromUIDListener callback) {
        userRef.document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                callback.onComplete(documentSnapshot.toObject(User.class));
            } else {
                Log.d(TAG, String.format("UID: %s not exist", uid));
                callback.onComplete(documentSnapshot.toObject(User.class));
            }
        });
    }


    public void countEventComment(String event_id, CountEventCommentListener callback) {
        commentRef.whereEqualTo("event_id", event_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onComplete(String.valueOf(task.getResult().size()));
            }
        });
    }

    public void loadEventComment(String event_id, LoadEventCommentListener callback) {
        commentRef
            .whereEqualTo("event_id", event_id)
            .limit(5)
            .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<Comment> commentResult = new ArrayList<>();
                    for (DocumentSnapshot doc : task.getResult()) {
                        Comment commentItem = new Comment();
                        commentItem.setId(doc.getId());
                        commentItem.setComment(doc.get("comment").toString());
                        commentItem.setDislike(doc.get("dislike").toString());
                        commentItem.setLike(doc.get("like").toString());
                        commentItem.setTime(doc.get("time").toString());
                        commentItem.setUid(doc.get("uid").toString());
                        commentResult.add(commentItem);
                    }

                    SeriesIterator<Comment, Comment> seriesIterator = new SeriesIterator<Comment, Comment>(commentResult, new SeriesIterator.SeriesIteratorFunctions<Comment, Comment>() {
                        @Override
                        public void onEveryItem(SeriesIterator<Comment, Comment> context, Comment commentItem) {
                            // Joining user doc to comment
                            getUserDetailFromUID(commentItem.getUid(), userResult -> {
                                commentItem.setPict(userResult.getPhoto());
                                commentItem.setNick_name(userResult.getFirst_name());
                                context.next(commentItem);
                            });
                        }

                        @Override
                        public void onReturn(List<Comment> values) {
                            ArrayList<Comment> result = new ArrayList<>();
                            result.addAll(values);
                            callback.onComplete(result);
                        }
                    });
                    seriesIterator.execute();
                }
            });
    }

    public void sendEventComment(String event_id, String uid, String comment, TaskCompleteListener callback) {
        Map<String, Object> newComment = new HashMap<>();
        newComment.put("event_id", event_id);
        newComment.put("uid", uid);
        newComment.put("time", timeNow);
        newComment.put("comment", comment);
        newComment.put("like", "0");
        newComment.put("dislike", "0");

        commentRef.add(newComment).addOnCompleteListener(task -> {
            if (task.isComplete()) {
                callback.onComplete(task);
            }
        });
    }

    public void createTransaction(Event event, String uid, TaskCompleteListener callback) {
        Map<String, Object> transactionItem = new HashMap<>();
        transactionItem.put("event_id", event.getId());
        transactionItem.put("uid", uid);
        transactionItem.put("time", timeNow);

        int ticketSoldBeforeTransaction = Integer.parseInt(event.getTicket_sold());
        int ticketSoldAfterTransaction = ticketSoldBeforeTransaction + 1;

        transactionRef.add(transactionItem).addOnCompleteListener(task -> {
            if (task.isComplete()) {
                // Updating ticket sold value
                eventRef
                    .document(event.getId())
                    .update("ticket_sold", String.valueOf(ticketSoldAfterTransaction))
                    .addOnCompleteListener(getEventTask -> callback.onComplete(getEventTask));
            }
        });
    }

    public void doLikeEvent(String eventId, String uid, TaskCompleteListener callback) {
        Map<String, Object> likeItem = new HashMap<>();
        likeItem.put("event_id", eventId);
        likeItem.put("uid", uid);
        likeItem.put("time", timeNow);

        likeRef.add(likeItem).addOnCompleteListener(task -> {
            if (task.isComplete()) {
                callback.onComplete(task);
            }
        });
    }

    public void doUnlikeEvent(String eventId, String uid, TaskCompleteListener callback) {
        checkEventLiked(eventId, uid, commentId -> {
            if (commentId != null) {
                likeRef
                    .document(commentId)
                    .delete()
                    .addOnCompleteListener(task -> {

                        if (task.isComplete()) {
                            callback.onComplete(task);
                        }
                    });
            }
        });
    }

    public void updateUserPoint(String uid, String currentPoint, String additionalPoint, TaskCompleteListener callback) {
        int finalPoint = Integer.parseInt(currentPoint) + Integer.parseInt(additionalPoint);
        userRef
            .document(uid)
            .update("point", String.valueOf(finalPoint))
            .addOnCompleteListener(task -> callback.onComplete(task));
    }

    public void getRegisteredEvent(String uid, TaskCompleteListener callback) {
        commentRef.whereEqualTo("uid", uid).get().addOnCompleteListener(task -> {
            if (task.isComplete()) {
                callback.onComplete(task);
            }
        });
    }

    public interface CheckFieldExistListener {
        void onComplete(Boolean isExist);
    }

    public interface CheckLikedExistListener {
        void onComplete(String commentId);
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

    public interface TaskCompleteListener {
        void onComplete(Task task);
    }
}
