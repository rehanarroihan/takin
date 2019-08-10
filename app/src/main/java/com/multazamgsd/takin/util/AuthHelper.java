package com.multazamgsd.takin.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
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
import com.google.gson.GsonBuilder;
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
                .enableAutoManage(fragmentActivity, connectionResult ->
                        Toast.makeText(activity, "Connection error, please try again", Toast.LENGTH_LONG).show()
                ).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        activity.startActivityForResult(intent, RC_SIGN_IN);
    }

    public void firebaseAuthWithGoogle(final GoogleSignInAccount acct, FirebaseAuthWithGoogleListener callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(activity, signInWithCredentialTask -> {
            googleApiClient.stopAutoManage(fragmentActivity);
            googleApiClient.disconnect();
            if (signInWithCredentialTask.isComplete()) {
                callback.onComplete(signInWithCredentialTask);
            }
        });
    }

    public boolean isLoggedIn() {
        if (mAuth.getCurrentUser() == null) {
            return false;
        } else {
            return true;
        }
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
        YesNoDialog.YesNoDialogListener dialogListener = () -> {
            Prefs.clear();
            mAuth.signOut();
            activity.startActivity(new Intent(activity, LoginActivity.class));
            activity.finish();
        };
        YesNoDialog dialog = new YesNoDialog(activity, dialogListener);
        dialog.setMessage("Are you sure want to logout ?");
        dialog.show();
    }

    public interface FirebaseAuthWithGoogleListener {
        void onComplete(Task firebaseAuthWithGoogleTask);
    }
}
