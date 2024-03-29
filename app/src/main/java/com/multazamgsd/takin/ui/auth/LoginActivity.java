package com.multazamgsd.takin.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.multazamgsd.takin.R;
import com.multazamgsd.takin.model.LoginRequest;
import com.multazamgsd.takin.model.User;
import com.multazamgsd.takin.ui.event_detail.RegistrationActivity;
import com.multazamgsd.takin.ui.main.MainActivity;
import com.multazamgsd.takin.ui.main.SplashActivity;
import com.multazamgsd.takin.util.AuthHelper;
import com.multazamgsd.takin.util.DatabaseHelper;
import com.multazamgsd.takin.util.HideKeyboard;
import com.multazamgsd.takin.util.LoadingDialog;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btEmailSignIn;

    private String email, password;
    private FirebaseAuth mAuth;
    private AuthHelper authHelper;
    private DatabaseHelper mDatabaseHelper;
    private LoadingDialog ld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        authHelper = new AuthHelper(this);
        mDatabaseHelper = new DatabaseHelper();

        mAuth = FirebaseAuth.getInstance();

        ld = new LoadingDialog(this);

        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        TextView tvToSignUp = findViewById(R.id.textViewSignUp);
        btEmailSignIn = findViewById(R.id.buttonEmailSignIn);
        Button btGoogleSignIn = findViewById(R.id.buttonGoogleSignIn);

        email = etEmail.getText().toString();
        password = etEmail.getText().toString();

        //setting up toolbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        setTitle("Sign In");

        tvToSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
        btEmailSignIn.setOnClickListener(v -> {
            if(isValid()) {
                doEmailLogin();
            }
        });
        btGoogleSignIn.setOnClickListener(v -> authHelper.doGoogleRegister());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AuthHelper.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                authHelper.firebaseAuthWithGoogle(account, firebaseAuthWithGoogleTask -> {
                    if (firebaseAuthWithGoogleTask.isSuccessful()) {
                        ld.show();
                        // If userdata not exist in db, then beg user to sign up
                        mDatabaseHelper.checkFieldExist("user", mAuth.getCurrentUser().getUid(), isExist -> {
                            if (isExist) {
                                mDatabaseHelper.updateUserDataOnLogin(mAuth.getCurrentUser().getUid(), onComplete -> {
                                    ld.dismiss();
                                    if (onComplete.isSuccessful()) {
                                        startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(this, "Error updating data, please try again", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                ld.dismiss();
                                Toast.makeText(this, "You are not registered, please Sign Up first", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        ld.dismiss();
                        Toast.makeText(this, "Sign up error, please try again", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (ApiException e) {
                Toast.makeText(this,"Sign up error, please try again", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void doEmailLogin() {
        etEmail.setEnabled(false);
        etPassword.setEnabled(false);
        btEmailSignIn.setText("Signing in, please wait...");
        btEmailSignIn.setEnabled(false);

        HideKeyboard hideKeyboard = new HideKeyboard(this);
        hideKeyboard.run();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, signInEmailTask -> {
                    // Updating userdata (last login info)
                    if (signInEmailTask.isSuccessful()) {
                        mDatabaseHelper.updateUserDataOnLogin(mAuth.getCurrentUser().getUid(), updateUserDataTask -> {
                            if (updateUserDataTask.isSuccessful()) {
                                etEmail.setEnabled(true);
                                etPassword.setEnabled(true);
                                btEmailSignIn.setText("Sign In");
                                btEmailSignIn.setEnabled(true);

                                Toast.makeText(LoginActivity.this,"Sign in success", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this,"Updating data error, please try again", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(e -> {
                    etEmail.setEnabled(true);
                    etPassword.setEnabled(true);
                    btEmailSignIn.setText("Sign In");
                    btEmailSignIn.setEnabled(true);
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private boolean isValid() {
        boolean valid = true;

        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        if(email.isEmpty()) {
            etEmail.setError("This field is required");
            valid = false;
        } else {
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Invalid email");
                valid = false;
            }
        }

        if(password.isEmpty()) {
            etPassword.setError("This field is required");
            valid = false;
        }

        return valid;
    }
}
