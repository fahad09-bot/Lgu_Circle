package com.codesses.lgucircle.SplashActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.codesses.lgucircle.Utils.Constants;
import com.codesses.lgucircle.activity.AuthorityAC;
import com.codesses.lgucircle.activity.MainActivity;
import com.codesses.lgucircle.Authentication.LoginActivity;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.activity.Services.ServicesChatAC;
import com.codesses.lgucircle.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class SplashActivity extends AppCompatActivity {

    private Intent intent = null;
    AppCompatActivity mContext;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //      Hide status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getIntent().hasExtra(Constants.USER_ID))
        {
            intent = new Intent(mContext, ServicesChatAC.class);
            intent.putExtra(Constants.USER_ID, getIntent().getStringExtra(Constants.USER_ID));
        }
        if (FirebaseRef.getCurrentUser() != null) {
            mRef = FirebaseRef.getUserRef()
                    .child(FirebaseRef.getUserId());
            checkUserType();

        } else {

            intent = new Intent(SplashActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }

    //    TODO: Value event listener for user role check
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

            User user = snapshot.getValue(User.class);
            if (FirebaseRef.getCurrentUser().isEmailVerified()) {
                if (user.getType().equals("authority")) {
                    intent = new Intent(mContext, AuthorityAC.class);
                } else
                    intent = new Intent(mContext, MainActivity.class);
            } else {
                intent = new Intent(mContext, LoginActivity.class);
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {
            Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void checkUserType() {
        mRef.addValueEventListener(valueEventListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (FirebaseRef.getCurrentUser() != null)
            mRef.removeEventListener(valueEventListener);
    }
}