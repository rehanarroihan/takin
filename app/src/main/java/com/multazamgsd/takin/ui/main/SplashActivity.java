package com.multazamgsd.takin.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.multazamgsd.takin.R;
import com.multazamgsd.takin.ui.auth.BiodataActivity;
import com.multazamgsd.takin.ui.auth.LoginActivity;
import com.multazamgsd.takin.util.AuthHelper;
import com.multazamgsd.takin.util.DatabaseHelper;
import com.multazamgsd.takin.util.GlobalConfig;
import com.pixplicity.easyprefs.library.Prefs;

public class SplashActivity extends AppCompatActivity {
    private AuthHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        authHelper = new AuthHelper(this);
        if(authHelper.isLoggedIn()) {
            // Getting userdata
            new DatabaseHelper().getUserDetailFromUID(authHelper.getCurrentUser().getUid(), userResult -> {
                authHelper.updateUserdata(userResult);
                if (userResult.getFirst_name().equals("")) {
                    startActivity(new Intent(this, BiodataActivity.class));
                    finish();
                    return;
                }
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
