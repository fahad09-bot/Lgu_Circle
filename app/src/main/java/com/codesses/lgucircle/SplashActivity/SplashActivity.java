package com.codesses.lgucircle.SplashActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.codesses.lgucircle.activity.MainActivity;
import com.codesses.lgucircle.Authentication.LoginActivity;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;

public class SplashActivity extends AppCompatActivity {

    private Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (FirebaseRef.getCurrentUser() != null) {

            intent = new Intent(SplashActivity.this, MainActivity.class);

        } else {

            intent = new Intent(SplashActivity.this, LoginActivity.class);

        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(intent);
                }
            }
        }, 3000);
    }
}