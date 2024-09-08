package com.example.project_mad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_mad.LoginActivity;
import com.example.project_mad.MainActivity;
import com.example.project_mad.R;

public class SplashScreen extends AppCompatActivity {

    public static int SPLASH_TIMER = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if the user is logged in
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

                Intent intent;
                if (isLoggedIn) {
                    intent = new Intent(SplashScreen.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashScreen.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMER);
    }
}
