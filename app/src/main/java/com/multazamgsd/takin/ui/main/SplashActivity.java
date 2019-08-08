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
        String institution = Prefs.getString(GlobalConfig.INSTITUTION_PREFS, null);
        if(authHelper.isLoggedIn()) {
            // User is need to re-download userdata
            if (institution == null) {
                // Getting userdata
                new DatabaseHelper().getUserDetailFromUID(authHelper.getCurrentUser().getUid(), userResult -> {
                    authHelper.updateUserdata(userResult);
                    // User is need to fill bio
                    if (isNeedFillBiodata(userResult.getInstitution())) {
                        startActivity(new Intent(this, BiodataActivity.class));
                        finish();
                        return;
                    }
                    // User no need fill bio
                    toMainScreen();
                });
            } else {
                // User is need to fill bio
                if (isNeedFillBiodata(institution)) {
                    startActivity(new Intent(this, BiodataActivity.class));
                    finish();
                    return;
                } else {
                    // Data is okay
                    toMainScreen();
                }
            }
        } else {
            // if user not authorized, then go to login activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void toMainScreen() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private boolean isNeedFillBiodata(String param) {
        if (param == null) {
            return true;
        } else {
            if (param.equals("")) {
                return true;
            } else {
                return false;
            }
        }
    }
}
