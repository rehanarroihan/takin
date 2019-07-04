package com.multazamgsd.takin;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.multazamgsd.takin.util.AuthHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private AuthHelper authHelper;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_seminar:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_committee:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
                case R.id.navigation_contest:
                    mTextMessage.setText("Contest");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        authHelper = new AuthHelper(this);
        if(authHelper.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        mTextMessage.setText("Welcome, " + authHelper.getCurrentUser().getEmail());

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> authHelper.doLogout());
    }

}
