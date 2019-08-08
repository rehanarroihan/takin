package com.multazamgsd.takin.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.multazamgsd.takin.model.User;
import com.multazamgsd.takin.ui.auth.LoginActivity;
import com.multazamgsd.takin.ui.main.MainActivity;
import com.multazamgsd.takin.R;
import com.pixplicity.easyprefs.library.Prefs;

/*
* Commonly accessed from no need auth activity
* */

public class AuthHelper {
    private Activity activity;
    private FirebaseAuth mAuth;
    private GoogleApiClient googleApiClient;
    private FragmentActivity fragmentActivity;
    private DatabaseHelper mDatabaseHelper;

    public static final int RC_SIGN_IN = 77;

    public AuthHelper(Activity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        this.fragmentActivity = (FragmentActivity) activity;
        mDatabaseHelper = new DatabaseHelper();
    }

    public void doGoogleRegister() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(fragmentActivity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(activity, "Connection error, please try again", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        activity.startActivityForResult(intent, RC_SIGN_IN);
    }

    public void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(activity, task -> {
            googleApiClient.stopAutoManage(fragmentActivity);
            googleApiClient.disconnect();
            if (task.isSuccessful()) {
                User user = new User();
                user.setUid(mAuth.getCurrentUser().getUid());
                user.setAuth_type("google");
                user.setFirst_name(mAuth.getCurrentUser().getEmail().split("@")[0]);
                user.setLast_name("");
                user.setInstitution("");
                user.setId_no("");
                user.setPhone_number("");
                user.setPhoto("");
                user.setLast_login(new StringHelper().timeNow());
                user.setPoint("0");
                user.setPassword("");
                user.setEmail(mAuth.getCurrentUser().getEmail());
                mDatabaseHelper.updateUserData(user, onComplete -> {
                    if (onComplete.isSuccessful()) {
                        Toast.makeText(activity,"Sign up success", Toast.LENGTH_LONG).show();
                        activity.startActivity(new Intent(activity, MainActivity.class));
                        activity.finish();
                    }
                });
            } else {
                Toast.makeText(activity,"Sign up error, please try again", Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean isLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public void updateUserdata(User user) {
        Prefs.putString(GlobalConfig.UID_PREFS, user.getUid());
        Prefs.putString(GlobalConfig.AUTH_TYPE_PREFS, user.getAuth_type());
        Prefs.putString(GlobalConfig.EMAIL_PREFS, user.getEmail());
        Prefs.putString(GlobalConfig.FIRST_NAME_PREFS, user.getFirst_name());
        Prefs.putString(GlobalConfig.LAST_NAME_PREFS, user.getLast_name());
        Prefs.putString(GlobalConfig.INSTITUTION_PREFS, user.getInstitution());
        Prefs.putString(GlobalConfig.ID_NO_PREFS, user.getId_no());
        Prefs.putString(GlobalConfig.PHONE_NUMBER_PREFS, user.getPhone_number());
        Prefs.putString(GlobalConfig.PHOTO_PREFS, user.getPhoto());
        Prefs.putString(GlobalConfig.LAST_LOGIN_PREFS, user.getLast_login());
        Prefs.putString(GlobalConfig.POINT_PREFS, user.getPoint());
        Prefs.putString(GlobalConfig.PASSWORD_PREFS, user.getPassword());
    }

    public FirebaseUser getCurrentUser() {
       return mAuth.getCurrentUser();
    }

    public void doLogout() {
        AlertDialog.Builder alertBuilder;
        alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder.setTitle("Logout");
        alertBuilder.setMessage("Are you sure want to logout ?");
        alertBuilder.setPositiveButton("Yes", (dialog, which) -> {
            mAuth.signOut();
            activity.startActivity(new Intent(activity, LoginActivity.class));
            activity.finish();
        });
        alertBuilder.setNegativeButton("No", (dialog, which) -> {

        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
}
