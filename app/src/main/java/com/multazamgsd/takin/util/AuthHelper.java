package com.multazamgsd.takin.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.multazamgsd.takin.LoginActivity;
import com.multazamgsd.takin.MainActivity;
import com.multazamgsd.takin.R;

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
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        googleApiClient.stopAutoManage(fragmentActivity);
                        googleApiClient.disconnect();

                        if (task.isSuccessful()) {
                            mDatabaseHelper.updateUserData(
                                    "google",
                                    mAuth.getCurrentUser().getEmail(),
                                    mAuth.getCurrentUser().getUid(),
                                    "",
                                    "",
                                    "",
                                    ""
                            );

                            Toast.makeText(activity,"Sign up success", Toast.LENGTH_LONG).show();
                            activity.startActivity(new Intent(activity, MainActivity.class));
                            activity.finish();
                        } else {
                            Toast.makeText(activity,"Sign up error, please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public boolean isLoggedIn() {
        return mAuth.getCurrentUser() == null;
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
