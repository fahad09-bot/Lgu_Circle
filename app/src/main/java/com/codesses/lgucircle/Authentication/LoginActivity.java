package com.codesses.lgucircle.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codesses.lgucircle.Dialogs.ForgotPassDialog;
import com.codesses.lgucircle.activity.MainActivity;
import com.codesses.lgucircle.Interfaces.OnForgotPass;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements OnForgotPass {

    @BindView(R.id.radio_group)
    RadioGroup Radio_group;
    @BindView(R.id.login_email)
    EditText Login_email;
    @BindView(R.id.login_pass)
    EditText Login_pass;
    @BindView(R.id.forgot_pass)
    TextView Forgot_pass;
    @BindView(R.id.new_user)
    Button New_user;
    @BindView(R.id.login_btn)
    Button Login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


//        TODO: Click Listeners
        New_user.setOnClickListener(this::createAccount);
        Login_btn.setOnClickListener(this::getUserData);
        Forgot_pass.setOnClickListener(this::forgotPass);


//        TODO: Radio Group Checked Listener
        Radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);

            }
        });

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
        String email = Login_email.getText().toString().trim();
        String pass = Login_pass.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
            login(email, pass);
        } else {
            Toast.makeText(this, "Fields Must be Filled", Toast.LENGTH_SHORT).show();
        }
    }

    private void login(String email, String pass) {
        FirebaseRef.getAuth()
                .signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseRef.getCurrentUser();

                            if (user.isEmailVerified()) {

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                finish();
                                startActivity(intent);
                            } else {

//                                TODO: Sign Out For Email Verification
                                FirebaseRef.getAuth().signOut();
                                user.sendEmailVerification();

                                Toast.makeText(LoginActivity.this, getString(R.string.email_verify_msg), Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void forgotPass(View view) {
        ForgotPassDialog forgotPassDialog = new ForgotPassDialog();
        forgotPassDialog.show(getSupportFragmentManager(), "Forgot Pass dialog");
    }


}