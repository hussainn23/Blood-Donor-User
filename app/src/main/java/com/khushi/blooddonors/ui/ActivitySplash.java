package com.khushi.blooddonors.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.khushi.blooddonors.R;
import com.khushi.blooddonors.SharedPrefManager;

public class ActivitySplash extends AppCompatActivity {

    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPrefManager = new SharedPrefManager(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPrefManager.isLoggedIn()) {
                    // If already logged in, move to the main activity
                    Intent intent = new Intent(ActivitySplash.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // If not logged in, move to the login activity
                    Intent intent = new Intent(ActivitySplash.this, ActivityLogin.class);
                    startActivity(intent);
                    finish();
                }
                finish();
            }
        }, 4000);
    }
}
