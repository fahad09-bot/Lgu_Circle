package com.codesses.lgucircle.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.codesses.lgucircle.Dialogs.ForgotPassDialog;
import com.codesses.lgucircle.Dialogs.ProgressDialog;
import com.codesses.lgucircle.Interfaces.OnForgotPass;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.Utils.SharedPrefManager;
import com.codesses.lgucircle.activity.AuthorityAC;
import com.codesses.lgucircle.activity.MainActivity;
import com.codesses.lgucircle.databinding.ActivityLoginBinding;
import com.codesses.lgucircle.Enums.SharedPrefKey;
import com.codesses.lgucircle.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements OnForgotPass {

    private AppCompatActivity mContext;

    private ActivityLoginBinding binding;

    String userEmail = "", userPassword = "";
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        binding = DataBindingUtil.setContentView(mContext, R.layout.activity_login);


//        TODO: Click Listeners
        binding.llNewUser.setOnClickListener(this::createAccount);
        binding.loginBtn.setOnClickListener(this::getUserData);

//        TODO: Text Change Listeners
        setTextChangeListeners();
    }

    private void setTextChangeListeners() {
        binding.loginEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() != 0) {
                    userEmail = s.toString();
                    setButtonEnabled(userEmail, userPassword);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.loginPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() != 0) {
                    userPassword = s.toString();
                    setButtonEnabled(userEmail, userPassword);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setButtonEnabled(String userEmail, String userPassword) {
        if (!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPassword))
            binding.loginBtn.setEnabled(true);
        else
            binding.loginBtn.setEnabled(false);
    }

    //forgot password
    @Override
    public void onApply(String email) {
        FirebaseRef.getAuth()
                .sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Check Your Email", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Alert! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void createAccount(View view) {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }

    private void getUserData(View view) {
        String email = binding.loginEmail.getText().toString().trim();
        String pass = binding.loginPass.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
            ProgressDialog.ShowProgressDialog(mContext, R.string.signing_in, R.string.please_wait);
            getUserType(email, pass);
        } else {
            Toast.makeText(this, "Fields Must be Filled", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserType(String email, String pass) {
        query = FirebaseRef.getUserRef().orderByChild("email").equalTo(email);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User model = snapshot.getValue(User.class);
                        SharedPrefManager.getInstance(mContext).storeSharedData(SharedPrefKey.USER, model);


                        if (model.getType().equals("authority"))
                            login(AuthorityAC.class, email, pass);
                        else
                            login(MainActivity.class, email, pass);
                    }
                } else {

                    ProgressDialog.DismissProgressDialog();
                    Toast.makeText(LoginActivity.this, "Alert! " + getString(R.string.not_register_user), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                ProgressDialog.DismissProgressDialog();
                Toast.makeText(LoginActivity.this, "Alert! " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login(Class activity, String email, String pass) {
        FirebaseRef.getAuth()
                .signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseRef.getCurrentUser();

                        if (user.isEmailVerified()) {
                            updateToken(activity);

                        } else {
                            ProgressDialog.DismissProgressDialog();
//                                TODO: Sign Out For Email Verification
                            FirebaseRef.getAuth().signOut();
                            user.sendEmailVerification();

                            Toast.makeText(LoginActivity.this, getString(R.string.email_verify_msg), Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        ProgressDialog.DismissProgressDialog();
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateToken(Class activity) {
        Map<String, Object> map = new HashMap<>();
        map.put("fcmToken", SharedPrefManager.getInstance(mContext).getSharedData(SharedPrefKey.TOKEN));

        FirebaseRef
                .getUserRef()
                .child(FirebaseRef.getCurrentUserId())
                .updateChildren(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        ProgressDialog.DismissProgressDialog();
                        Intent intent = new Intent(LoginActivity.this, activity);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        ProgressDialog.DismissProgressDialog();
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void forgotPass(View view) {
        ForgotPassDialog forgotPassDialog = new ForgotPassDialog();
        forgotPassDialog.show(getSupportFragmentManager(), "Forgot Pass dialog");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}