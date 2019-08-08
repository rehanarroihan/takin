package com.multazamgsd.takin.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.multazamgsd.takin.R;
import com.multazamgsd.takin.model.User;
import com.multazamgsd.takin.ui.main.MainActivity;
import com.multazamgsd.takin.ui.main.SplashActivity;
import com.multazamgsd.takin.util.AuthHelper;
import com.multazamgsd.takin.util.DatabaseHelper;
import com.multazamgsd.takin.util.HideKeyboard;
import com.multazamgsd.takin.util.StringHelper;

public class RegisterActivity extends AppCompatActivity {
    private User newUser;
    private ProgressDialog pd;

    private EditText etEmail, etPassword;
    private Button btEmailSignUp;

    private String email, password;
    private FirebaseAuth mAuth;
    private AuthHelper authHelper;
    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        authHelper = new AuthHelper(this);
        mDatabaseHelper = new DatabaseHelper();

        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        TextView tvToSignIn = findViewById(R.id.textViewSignIn);
        btEmailSignUp = findViewById(R.id.buttonEmailSignUp);
        Button btGoogleSignUp = findViewById(R.id.buttonGoogleSignUp);

        // Setting up toolbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        setTitle("Sign Up");

        pd = new ProgressDialog(this);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.setIndeterminate(true);

        // Setting up initial data for user info
        newUser = new User();
        newUser.setUid("");
        newUser.setAuth_type("");
        newUser.setFirst_name("");
        newUser.setLast_name("");
        newUser.setInstitution("");
        newUser.setId_no("");
        newUser.setPhone_number("");
        newUser.setPhoto("");
        newUser.setLast_login("");
        newUser.setPoint("");
        newUser.setPassword("");
        newUser.setEmail("");

        tvToSignIn.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
        btEmailSignUp.setOnClickListener(v -> {
            if(isValid()) {
                doEmailRegister();
            }
        });
        btGoogleSignUp.setOnClickListener(v -> authHelper.doGoogleRegister());
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
                        pd.show();
                        // Checking if userdata is exist, then beg user to login
                        mDatabaseHelper.checkFieldExist("user", mAuth.getCurrentUser().getUid(), isExist -> {
                            if (isExist) {
                                pd.dismiss();
                                Toast.makeText(this, "You are already registered, please Sign In", Toast.LENGTH_LONG).show();
                            } else {
                                newUser.setUid(mAuth.getCurrentUser().getUid());
                                newUser.setAuth_type("google");
                                newUser.setFirst_name(mAuth.getCurrentUser().getEmail().split("@")[0]);
                                newUser.setLast_login(new StringHelper().timeNow());
                                newUser.setPoint("0");
                                newUser.setEmail(mAuth.getCurrentUser().getEmail());
                                mDatabaseHelper.updateUserData(newUser, onComplete -> {
                                    if (onComplete.isSuccessful()) {
                                        pd.dismiss();
                                        Toast.makeText(this, "Sign up success", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterActivity.this, SplashActivity.class));
                                        finish();
                                    }
                                });
                            }
                        });
                    } else {
                        pd.dismiss();
                        Toast.makeText(this,"Sign up error, please try again", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (ApiException e) {
                Toast.makeText(this,"Sign up error, please try again", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void doEmailRegister() {
        etEmail.setEnabled(false);
        etPassword.setEnabled(false);
        btEmailSignUp.setText("Signing up, please wait...");
        btEmailSignUp.setEnabled(false);

        HideKeyboard hideKeyboard = new HideKeyboard(this);
        hideKeyboard.run();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, createUserTask -> {
                    if (createUserTask.isSuccessful()) {
                        newUser.setUid(mAuth.getCurrentUser().getUid());
                        newUser.setAuth_type("email");
                        newUser.setFirst_name(mAuth.getCurrentUser().getEmail().split("@")[0]);
                        newUser.setLast_login(new StringHelper().timeNow());
                        newUser.setPoint("0");
                        newUser.setPassword(password);
                        newUser.setEmail(mAuth.getCurrentUser().getEmail());
                        mDatabaseHelper.updateUserData(newUser, updateUserDataTask -> {
                            etEmail.setEnabled(true);
                            etPassword.setEnabled(true);
                            btEmailSignUp.setText("Sign Up");
                            btEmailSignUp.setEnabled(true);

                            if (updateUserDataTask.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this,"Sign up success", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegisterActivity.this, SplashActivity.class));
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this,"Error updating data, please try again", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(e -> {
                    etEmail.setEnabled(true);
                    etPassword.setEnabled(true);
                    btEmailSignUp.setText("Sign Up");
                    btEmailSignUp.setEnabled(true);

                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
