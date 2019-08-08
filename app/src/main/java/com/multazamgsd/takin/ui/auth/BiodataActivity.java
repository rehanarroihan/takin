package com.multazamgsd.takin.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.multazamgsd.takin.R;
import com.multazamgsd.takin.ui.main.SplashActivity;
import com.multazamgsd.takin.util.AuthHelper;
import com.multazamgsd.takin.util.DatabaseHelper;
import com.multazamgsd.takin.util.GlobalConfig;
import com.pixplicity.easyprefs.library.Prefs;

public class BiodataActivity extends AppCompatActivity {
    private EditText etEmail, etFirstName, etLastName, etIdNo, etInstitution, etPhone;
    private String uid, firstName, lastName, idNo, institution, phone;
    private Button btFinish;
    private ProgressDialog progressBar;
    private AuthHelper mAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biodata);
        progressBar = new ProgressDialog(this);

        // Setting up toolbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        setTitle("Biodata");

        // Set up UI
        etEmail = findViewById(R.id.editTextEmail);
        etFirstName = findViewById(R.id.editTextFirstName);
        etLastName = findViewById(R.id.editTextLastName);
        etIdNo = findViewById(R.id.editTextIdNo);
        etInstitution = findViewById(R.id.editTextInstitution);
        etPhone = findViewById(R.id.editTextPhone);
        btFinish = findViewById(R.id.buttonSubmitBiodata);

        mAuthHelper = new AuthHelper(this);
        uid = new AuthHelper(BiodataActivity.this).getCurrentUser().getUid();

        btFinish.setOnClickListener(view -> {
            if (isValid()) {
                progressBar.setMessage("Please wait...");
                progressBar.setCancelable(false);
                progressBar.setIndeterminate(true);
                progressBar.show();

                submit(submitTask -> {
                    if (submitTask.isSuccessful()) {
                        // Verifying new data from server, then save to session
                        new DatabaseHelper().getUserDetailFromUID(uid, userResult -> {
                            mAuthHelper.updateUserdata(userResult);
                            progressBar.dismiss();
                            startActivity(new Intent(BiodataActivity.this, SplashActivity.class));
                            finish();
                        });
                    } else {
                        Toast.makeText(BiodataActivity.this, "Cannot update data, please try again", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        setInitialData();
        Toast.makeText(BiodataActivity.this, "Please fulfill your personal data", Toast.LENGTH_LONG).show();
    }

    private void setInitialData() {
        etEmail.setText(mAuthHelper.getCurrentUser().getEmail());
        etFirstName.setText(Prefs.getString(GlobalConfig.FIRST_NAME_PREFS, null));
    }

    private boolean isValid() {
        boolean valid = true;

        firstName = etFirstName.getText().toString();
        lastName = etLastName.getText().toString();
        idNo = etIdNo.getText().toString();
        institution = etInstitution.getText().toString();
        phone = etPhone.getText().toString();

        if(firstName.isEmpty()) {
            etFirstName.setError(getResources().getString(R.string.field_required));
            valid = false;
        }

        if(lastName.isEmpty()) {
            etLastName.setError(getResources().getString(R.string.field_required));
            valid = false;
        }

        if(idNo.isEmpty()) {
            etIdNo.setError(getResources().getString(R.string.field_required));
            valid = false;
        }

        if(institution.isEmpty()) {
            etInstitution.setError(getResources().getString(R.string.field_required));
            valid = false;
        }

        if(phone.isEmpty()) {
            etPhone.setError(getResources().getString(R.string.field_required));
            valid = false;
        }

        return valid;
    }

    private void submit(SubmitListener callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user").document(uid)
                .update(
                        "id_no", idNo,
                        "institution", institution,
                        "first_name", firstName,
                        "last_name", lastName,
                        "phone_number", phone
                ).addOnCompleteListener(task -> callback.onComplete(task));
    }

    private interface SubmitListener {
        void onComplete(Task task);
    }
}
