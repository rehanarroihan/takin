package com.multazamgsd.takin;

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

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.multazamgsd.takin.ui.main.MainActivity;
import com.multazamgsd.takin.util.AuthHelper;
import com.multazamgsd.takin.util.DatabaseHelper;
import com.multazamgsd.takin.util.HideKeyboard;

public class RegisterActivity extends AppCompatActivity {
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

        //setting up toolbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        setTitle("Sign Up");

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
                authHelper.firebaseAuthWithGoogle(account);
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
                .addOnCompleteListener(this, task -> {
                    etEmail.setEnabled(true);
                    etPassword.setEnabled(true);
                    btEmailSignUp.setText("Sign Up");
                    btEmailSignUp.setEnabled(true);

                    if (task.isSuccessful()) {
                        mDatabaseHelper.updateUserData(
                                "email",
                                mAuth.getCurrentUser().getEmail(),
                                mAuth.getCurrentUser().getUid(),
                                "",
                                mAuth.getCurrentUser().getEmail().split("@")[0],
                                "",
                                password
                        );
                        Toast.makeText(RegisterActivity.this,"Sign up success", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this,"Sign up error, please try again", Toast.LENGTH_LONG).show();
                    }
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
