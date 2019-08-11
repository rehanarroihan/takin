package com.multazamgsd.takin.ui.event_detail;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.multazamgsd.takin.R;
import com.multazamgsd.takin.model.Event;
import com.multazamgsd.takin.ui.main.MainActivity;
import com.multazamgsd.takin.ui.my_event.MyEventActivity;
import com.multazamgsd.takin.util.AuthHelper;
import com.multazamgsd.takin.util.DatabaseHelper;
import com.multazamgsd.takin.util.GlideApp;
import com.multazamgsd.takin.util.GlobalConfig;
import com.multazamgsd.takin.util.LoadingDialog;
import com.multazamgsd.takin.util.YesNoDialog;
import com.pixplicity.easyprefs.library.Prefs;

public class RegistrationActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT = "extra_event";

    private ImageView ivEvent;
    private EditText etEmail, etFirstName, etLastName, etIdNo, etInstitution, etPhone;
    private String uid, firstName, lastName, idNo, institution, phone;
    private Button btSubmit;
    private DatabaseHelper mDatabaseHelper;

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setPadding(12,0,24,0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Registration");

        event = getIntent().getParcelableExtra(EXTRA_EVENT);
        uid = new AuthHelper(this).getCurrentUser().getUid();
        mDatabaseHelper = new DatabaseHelper();

        // Set up UI
        etEmail = findViewById(R.id.editTextEmail);
        etFirstName = findViewById(R.id.editTextFirstName);
        etLastName = findViewById(R.id.editTextLastName);
        etIdNo = findViewById(R.id.editTextIdNo);
        etInstitution = findViewById(R.id.editTextInstitution);
        etPhone = findViewById(R.id.editTextPhone);
        btSubmit = findViewById(R.id.buttonSubmit);
        ivEvent = findViewById(R.id.imageViewDetailEvent);

        setUpBiodataForm();

        GlideApp.with(this)
                .load(event.getPhoto_url())
                .apply(RequestOptions
                        .placeholderOf(R.drawable.ic_image_grey_24dp)
                        .error(R.drawable.ic_image_grey_24dp))
                .into(ivEvent);
        btSubmit.setOnClickListener(v -> {
            if (isValid()) {
                showConfirmationDialog();
            }
        });
    }

    private void setUpBiodataForm() {
        etEmail.setText(Prefs.getString(GlobalConfig.EMAIL_PREFS, null));
        etFirstName.setText(Prefs.getString(GlobalConfig.FIRST_NAME_PREFS, null));
        etLastName.setText(Prefs.getString(GlobalConfig.LAST_NAME_PREFS, null));
        etInstitution.setText(Prefs.getString(GlobalConfig.INSTITUTION_PREFS, null));
        etIdNo.setText(Prefs.getString(GlobalConfig.ID_NO_PREFS, null));
        etPhone.setText(Prefs.getString(GlobalConfig.PHONE_NUMBER_PREFS, null));
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

    private void showConfirmationDialog() {
        YesNoDialog.YesNoDialogListener dialogListener = () -> submit();
        YesNoDialog dialog = new YesNoDialog(this, dialogListener);
        dialog.setMessage("Do you want to confirm\nthis data ?");
        dialog.show();
    }

    private void submit() {
        LoadingDialog ld = new LoadingDialog(RegistrationActivity.this);
        ld.show();
        mDatabaseHelper.createTransaction(event, uid, insertTask -> {
            if (insertTask.isSuccessful()) {
                // Add additional TAK point
                String userPoint = Prefs.getString(GlobalConfig.POINT_PREFS, null);
                mDatabaseHelper.updateUserPoint(uid, userPoint, event.getPoint(), updateTask -> {
                    if (updateTask.isSuccessful()) {
                        // Show success dialog
                        final Dialog dialog = new Dialog(this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_book_complete);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.setCancelable(false);

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                        ((TextView) dialog.findViewById(R.id.textViewMessage)).setText("Registration successful, go to My Event to access your ticket");

                        ((Button) dialog.findViewById(R.id.buttonMyEvent)).setOnClickListener(v -> {
                            startActivity(new Intent(RegistrationActivity.this, MyEventActivity.class));
                            finish();
                        });

                        mDatabaseHelper.getUserDetailFromUID(uid, userResult -> {
                            new AuthHelper(this).updateUserdata(userResult);
                            MainActivity.setPointInfo();
                            ld.dismiss();

                            dialog.show();
                            dialog.getWindow().setAttributes(lp);
                        });
                    } else {
                        ld.dismiss();
                        Toast.makeText(this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                ld.dismiss();
                Toast.makeText(this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
